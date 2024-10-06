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
import jakarta.servlet.http.Part;
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
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            Part dataToFilterPart = req.getPart("dataToFilter");
            Part sheetDimensionsPart = req.getPart("sheetDimension");

            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream dataToFilterInputStream = dataToFilterPart.getInputStream();
            InputStream sheetDimensionsInputStream = sheetDimensionsPart.getInputStream();

            String dataToFilterJson = new String(dataToFilterInputStream.readAllBytes());
            String sheetDimensionsJson = new String(sheetDimensionsInputStream.readAllBytes());

            Gson gson = new Gson();
            DataToFilterDto dataToFilterDto = gson.fromJson(dataToFilterJson, DataToFilterDto.class);
            RangeDto rangeDto = dataToFilterDto.getFilterRange();

            DimensionDto sheetDimensionDto = gson.fromJson(sheetDimensionsJson, DimensionDto.class);
            Dimension sheetDimensions = new DimensionImpl(sheetDimensionDto.getNumRows(),sheetDimensionDto.getNumCols(),sheetDimensionDto.getWidthCol(),sheetDimensionDto.getHeightRow());

            CellIdentifierImpl topLeft = new CellIdentifierImpl(rangeDto.getTopLeft());
            CellIdentifierImpl bottomRight = new CellIdentifierImpl(rangeDto.getBottomRight());

            Range range = new RangeImpl(rangeDto.getName(),topLeft,bottomRight,sheetDimensions);

            Map<String, List<String>> selectedValuesForCols = dataToFilterDto.getSelectedValuesForColumns();

            SheetDto sheetDto = engine.filterRangeByColumnsAndValues(range, selectedValuesForCols);
            String jsonResp = gson.toJson(sheetDto);
            resp.getWriter().write(jsonResp);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error filter range" + e.getMessage() + "\"}");

        }

    }
}
