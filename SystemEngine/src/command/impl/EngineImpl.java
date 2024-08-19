package command.impl;

import command.api.Engine;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class EngineImpl implements Engine {


    @Override
    public LoadDto loadFile(String path)
    {
        try {
            // Step 1: Set up JAXB context and unmarshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(SpreadsheetXml.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Step 2: Unmarshal the XML file into a SpreadsheetXml object
            SpreadsheetXml spreadsheetXml = (SpreadsheetXml) unmarshaller.unmarshal(new File(path));

            // Step 3: Map SpreadsheetXml to SpreadSheetImpl
            mapXmlToSpreadsheet(spreadsheetXml);

            // Step 4: Return LoadDto indicating success
            return new LoadDto(true, "File loaded successfully");

        } catch (JAXBException e) {
            e.printStackTrace();
            return new LoadDto(false, "Failed to load file: " + e.getMessage());
        }
    }

    @Override
    public SheetDto displaySpreadsheet() {
        return null;
    }

    @Override
    public CellDto displayCellValue(String cellid) {
        return null;
    }

    @Override
    public CellDto updateCell(String cellid) {
        return null;
    }

    @Override
    public VerDto displayVersions() {
        return null;
    }

    @Override
    public VerDto displaySheetByVersion(String version) {
        return null;
    }

    @Override
    public ExitDto exitSystem() {
        return null;
    }
}


