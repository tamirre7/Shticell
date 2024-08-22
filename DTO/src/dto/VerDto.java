package dto;

import java.util.Map;

public class VerDto {
    private final Map<Integer, SheetDto> versionSheetDtoMap;

    public VerDto(Map<Integer, SheetDto> versions) {
        this.versionSheetDtoMap = versions;
    }

    // Getter
    public Map<Integer, SheetDto> getVersions() {
        return versionSheetDtoMap;
    }
}
