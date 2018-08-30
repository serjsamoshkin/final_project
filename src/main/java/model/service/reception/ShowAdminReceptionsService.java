package model.service.reception;

import model.dao.DaoMapper;
import model.dao.reception.ReceptionDAO;
import model.dao.reception.ReviewDAO;
import model.entity.reception.Reception;
import model.entity.reception.Review;
import model.service.AbstractService;
import model.service.ServiceMapper;
import model.service.util.DataCheckerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistenceSystem.PersistException;
import persistenceSystem.criteria.CriteriaBuilder;
import util.dto.reception.ShowAdminReceptionsInDto;
import util.dto.reception.ShowAdminReceptionsOutDto;
import util.properties.PaginationPropertiesReader;
import util.wrappers.ReceptionView;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShowAdminReceptionsService extends AbstractService {

    private final Logger logger = LogManager.getLogger(ShowAdminReceptionsService.class);

    private DataCheckerService dataChecker = ServiceMapper.getMapper().getService(DataCheckerService.class);

    private Map<String, String> fieldMappingTable;{
        fieldMappingTable = new HashMap<>();
        fieldMappingTable.putIfAbsent("reception_day", "day");
        fieldMappingTable.putIfAbsent("reception_time", "time");
    }

    public ShowAdminReceptionsService(DataSource dataSource) {
        super(dataSource);
    }

    public ShowAdminReceptionsOutDto processShowReceptionRequest(ShowAdminReceptionsInDto inDto){

        ShowAdminReceptionsOutDto.ShowAdminReceptionsOutDtoBuilder builder = ShowAdminReceptionsOutDto.getBuilder();

        int page = 1;
        Optional<Integer> optInt = dataChecker.checkInteger(inDto.getPage());
        if (optInt.isPresent()){
            page = optInt.get();
        }

        String sortFieldParam = "reception_day";
        CriteriaBuilder.Order order = CriteriaBuilder.Order.ASC;
        String sortField;
        if (inDto.getSortBy().isPresent()){
            if (!inDto.getDirection().isPresent()){
                return builder.buildFalse();
            }
            sortFieldParam = inDto.getSortBy().get();
            try {
                order = CriteriaBuilder.Order.valueOf(inDto.getDirection().get().toUpperCase());
            }catch (Exception e){
                logger.error("incorrect order value in parameter Direction: " + inDto.getDirection().get(), e);
                return builder.buildFalse();
            }
        }
        sortField = fieldMappingTable.get(sortFieldParam);
        if (sortField == null){
            logger.error("sort_by parameter is null");
            builder.buildFalse();
        }
        builder.setSortingField(sortFieldParam);
        builder.setOrder(order);

        ReceptionDAO dao = DaoMapper.getMapper().getDao(ReceptionDAO.class);

        List<Reception> receptions;
        Map<Reception, Review> receptionReviewMap;
        int pageCount;
        try (Connection connection = getDataSource().getConnection()){
            int receptionCount = dao.getReceptionsCount(connection);

            int rowsForPage = Integer.valueOf(PaginationPropertiesReader.getInstance()
                    .getPropertyValue("admin_reception_count"));

            pageCount = receptionCount / rowsForPage + (receptionCount % rowsForPage == 0 ? 0 : 1);
            if (page > pageCount) {
                page = 1;
            }
            receptions = dao.getReceptionsWithLimitOrderedBy(connection, page, sortField, order);
            receptionReviewMap = DaoMapper.getMapper().getDao(ReviewDAO.class).
                    getReviewsByReceptions(receptions, connection).stream().
                    collect(Collectors.toMap(Review::getReception, Function.identity()));

        }catch (SQLException e){
            throw new PersistException(e);
        }

        builder.setPage(page);
        builder.setPageCount(pageCount);
        builder.setReceptions(receptions.stream().map(reception -> ReceptionView.of(reception, receptionReviewMap))
                .collect(Collectors.toList()));

        return builder.build();
    }



}


