package shticell.server.sheetpanel.servlets.commands.sortandfilter;

import com.google.gson.Gson;
import command.api.Engine;
import dto.DataToSortDto;
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

@WebServlet(name = "SortServlet", urlPatterns = {"/sheetview/sort"})
public class SortServlet extends HttpServlet {

    // Handles POST requests for sorting data
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

            // Read the input stream containing the sort data JSON
            InputStream dataToSortInputStream = req.getInputStream();
            String dataToSortJson = new String(dataToSortInputStream.readAllBytes());

            // Initialize Gson for JSON processing
            Gson gson = new Gson();

            // Deserialize the JSON data into a DataToSortDto object
            DataToSortDto dataToSortDto = gson.fromJson(dataToSortJson, DataToSortDto.class);

            // Extract relevant data from the DTO
            String sheetName = dataToSortDto.getSheetName();
            DimensionDto sheetDimensionDto = dataToSortDto.getDimensions();

            // Create a Dimension object based on the extracted dimensions
            Dimension sheetDimensions = new DimensionImpl(
                    sheetDimensionDto.getNumRows(),
                    sheetDimensionDto.getNumCols(),
                    sheetDimensionDto.getWidthCol(),
                    sheetDimensionDto.getHeightRow()
            );

            // Extract the range and columns to sort from the DTO
            RangeDto rangeDto = dataToSortDto.getSortRange();
            List<String> colsToSort = dataToSortDto.getColumnsToSort();

            // Create CellIdentifier objects for the top-left and bottom-right corners of the range
            CellIdentifierImpl topLeft = new CellIdentifierImpl(rangeDto.getTopLeft());
            CellIdentifierImpl bottomRight = new CellIdentifierImpl(rangeDto.getBottomRight());

            // Create a Range object based on the provided range data
            Range range = new RangeImpl(rangeDto.getName(), topLeft, bottomRight, sheetDimensions);

            // Use the engine to sort the specified range based on the selected columns
            SheetDto sheetDto = engine.sortRange(range, colsToSort, sheetName);
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
