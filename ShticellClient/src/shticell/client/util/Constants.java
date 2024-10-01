package shticell.client.util;

public class Constants {
    //fxml locations
    public final static String SHEET_HUB_MAIN_PAGE_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/main/shticell-hub-main.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/components/login/login-page.fxml";
    public final static String LOAD_SHEET_PAGE_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/components/loadsheet/load-sheet.fxml";
    public final static String AVAILABLE_SHEETS_PAGE_RESOURCE_LOCATION = "/shticell/client/sheethub/components/available/sheets/available-sheet.fxml";


    //server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/ShticellServer";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    private final static String HUB_SERVER_PATH = "/sheethub";
    private final static String SHEET_VIEW_SERVER_PATH = "/sheetview";

    //sheet hub resources
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String LOGOUT_PAGE = FULL_SERVER_PATH + HUB_SERVER_PATH + "/logout";
    public final static String LOAD_SHEET_PAGE = FULL_SERVER_PATH + HUB_SERVER_PATH + "/loadsheet";


    //sheet view resources
    public final static String UPDATE_CELL_PAGE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/updatecell";
    public final static String LATEST_VERSION_PAGE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/latestversion";

}
