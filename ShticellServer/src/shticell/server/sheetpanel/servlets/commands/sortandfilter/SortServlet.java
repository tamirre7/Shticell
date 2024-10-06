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
import jakarta.servlet.http.Part;
import shticell.server.utils.ServletUtils;
import spreadsheet.api.Dimension;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;
import spreadsheet.range.impl.RangeImpl;
import spreadsheet.sheetimpl.DimensionImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@WebServlet(name = "SortServlet", urlPatterns = {"/sheetview/sort"})
public class SortServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream dataToSortInputStream = req.getInputStream();

            String dataToSortJson = new String(dataToSortInputStream.readAllBytes());

            Gson gson = new Gson();

            DataToSortDto dataToSortDto = gson.fromJson(dataToSortJson, DataToSortDto.class);

            DimensionDto sheetDimensionDto = dataToSortDto.getDimensions();
            Dimension sheetDimensions = new DimensionImpl(sheetDimensionDto.getNumRows(),sheetDimensionDto.getNumCols(),sheetDimensionDto.getWidthCol(),sheetDimensionDto.getHeightRow());

            RangeDto rangeDto = dataToSortDto.getSortRange();
            List<String> colsToSort = dataToSortDto.getColumnsToSort();

            CellIdentifierImpl topLeft = new CellIdentifierImpl(rangeDto.getTopLeft());
            CellIdentifierImpl bottomRight = new CellIdentifierImpl(rangeDto.getBottomRight());

            Range range = new RangeImpl(rangeDto.getName(),topLeft,bottomRight,sheetDimensions);


            SheetDto sheetDto = engine.sortRange(range, colsToSort);
            String jsonResp = gson.toJson(sheetDto);
            resp.getWriter().write(jsonResp);
        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error sort range" + e.getMessage() + "\"}");
        }


    }
}
