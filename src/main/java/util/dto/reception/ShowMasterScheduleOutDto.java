package util.dto.reception;

import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;
import util.wrappers.ReceptionView;

import java.util.Map;

public class ShowMasterScheduleOutDto extends AbstractDto{

    private final Map<String, ReceptionView> schedule;

    private ShowMasterScheduleOutDto(boolean ok, Map<String, ReceptionView> schedule) {
        super(ok);
        this.schedule = schedule;
    }

    public Map<String, ReceptionView> getSchedule() {
        return schedule;
    }

    public static ShowMasterScheduleOutDtoBuilder getBuilder(){
        return new ShowMasterScheduleOutDtoBuilder();
    }

    public static class ShowMasterScheduleOutDtoBuilder extends AbstractDtoBuilder{
        private Map<String, ReceptionView> schedule;


        public ShowMasterScheduleOutDtoBuilder setSchedule(Map<String, ReceptionView> schedule) {
            this.schedule = schedule;
            return this;
        }

        @Override
        public ShowMasterScheduleOutDto build() {
            return build(true);
        }

        protected ShowMasterScheduleOutDto build(boolean buildOk){
            return new ShowMasterScheduleOutDto(buildOk, schedule);
        }

    }


}
