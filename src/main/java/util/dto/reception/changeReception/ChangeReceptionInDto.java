package util.dto.reception.changeReception;

import util.dto.AbstractDto;
import util.dto.AbstractDtoBuilder;

import java.util.Optional;

public class ChangeReceptionInDto extends AbstractDto{

    private final String id;
    private final String version;
    private final String status;


    private ChangeReceptionInDto(boolean ok, String id, String version, String status) {
        super(ok);
        this.id = id;
        this.version = version;
        this.status = status;
    }

    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<String> getStatus() {
        return Optional.ofNullable(status);
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public static ChangeReceptionInDtoBuilder getBuilder(){
        return new ChangeReceptionInDtoBuilder();
    }

    public static class ChangeReceptionInDtoBuilder extends AbstractDtoBuilder{
        private String id;
        private String status;
        private String version;


        public ChangeReceptionInDtoBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public ChangeReceptionInDtoBuilder setStatus(String status) {
            this.status = status;
            return this;
        }

        public ChangeReceptionInDtoBuilder setVersion(String version) {
            this.version = version;
            return this;
        }

        @Override
        public ChangeReceptionInDto build() {
            return build(true);
        }

        public ChangeReceptionInDto build(boolean buildOk){
            return new ChangeReceptionInDto(buildOk, id, version, status);
        }

    }


}
