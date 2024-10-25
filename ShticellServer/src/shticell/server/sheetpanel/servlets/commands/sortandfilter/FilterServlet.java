package shticell.server.sheetpanel.servlets.commands.sortandfilter;

import com.google.gson.Gson;
import command.api.Engine;
import dto.DataToFilterDto;
import dto.DimensionDto;
import dto.RangeDto;
import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import spreadsheet.api.Dimension;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;
import spreadsheet.range.impl.RangeImpl;
import spreadsheet.sheetimpl.DimensionImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@WebServlet(name = "FilterServlet", urlPatterns = {"/sheetview/filter"})
public class FilterServlet extends HttpServlet {

    // Handles POST requests to filter data
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set the response content type to JSON
        resp.setContentType("application/json");
        try {
            // Obtain the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            // Read the input stream containing the filter data JSON
            InputStream dataToFilterInputStream = req.getInputStream();
            String dataToFilterJson = new String(dataToFilterInputStream.readAllBytes());

            Gson gson = new Gson();
            // Deserialize the JSON data into a DataToFilterDto object
            DataToFilterDto dataToFilterDto = gson.fromJson(dataToFilterJson, DataToFilterDto.class);

            // Extract relevant data from the DTO
            String sheetName = dataToFilterDto.getSheetName();
            RangeDto rangeDto = dataToFilterDto.getFilterRange();
            DimensionDto sheetDimensionDto = dataToFilterDto.getDimension();

            // Create a Dimension object based on the extracted dimensions
            Dimension sheetDimensions = new DimensionImpl(
                    sheetDimensionDto.getNumRows(),
                    sheetDimensionDto.getNumCols(),
                    sheetDimensionDto.getWidthCol(),
                    sheetDimensionDto.getHeightRow()
            );

            // Create CellIdentifier objects for the top-left and bottom-right corners of the range
            CellIdentifierImpl topLeft = new CellIdentifierImpl(rangeDto.getTopLeft());
            CellIdentifierImpl bottomRight = new CellIdentifierImpl(rangeDto.getBottomRight());

            // Create a Range object based on the provided range data
            Range range = new RangeImpl(rangeDto.getName(), topLeft, bottomRight, sheetDimensions);

            // Get the selected values for filtering columns
            Map<String, List<String>> selectedValuesForCols = dataToFilterDto.getSelectedValuesForColumns();

            // Use the engine to filter the specified range by the selected values
            SheetDto sheetDto = engine.filterRangeByColumnsAndValues(range, selectedValuesForCols, sheetName);
            // Serialize the resulting SheetDto to JSON and write it to the response
            String jsonResp = gson.toJson(sheetDto);
            resp.getWriter().write(jsonResp);

        } catch (Exception e) {
            // Handle exceptions by setting the response status to 500 and writing the error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(e.getMessage());
        }
    }
}
