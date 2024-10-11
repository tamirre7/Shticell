package shticell.client.util;

public class Constants {

    //fxml locations
    public final static String SHEET_HUB_MAIN_PAGE_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/main/shticell-hub-main.fxml";
    public final static String SHEET_VIEW_MAIN_PAGE_FXML_RESOURCE_LOCATION = "/shticell/client/sheetpanel/main/shticellApp.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/components/login/login-page.fxml";
    public final static String LOAD_SHEET_PAGE_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/components/loadsheet/load-sheet.fxml";
    public final static String AVAILABLE_SHEETS_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/components/available/sheets/available-sheets.fxml";
    public final static String PERMISSION_TABLE_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/components/permission/table/permission-table.fxml";
    public final static String COMMANDS_MENU_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/components/commands/components/commands-menu.fxml";
    public final static String FORMULA_BUILDER_FXML_RESOURCE_LOCATION = "/shticell/client/sheetpanel/command/components/formulabuilder/formulabuilder.fxml";
    public final static String PERMISSION_REQUEST_RESOURCE_LOCATION = "/shticell/client/sheethub/components/commands/components/permissionrequest/request-permission.fxml";
    public final static String PERMISSION_RESPONSE_RESOURCE_LOCATION = "/shticell/client/sheethub/components/commands/components/permissionresponse/permission-response.fxml";

    //server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/ShticellServer";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    private final static String HUB_SERVER_PATH = "/sheethub";
    private final static String SHEET_VIEW_SERVER_PATH = "/sheetview";
    private final static String PERMISSION_PATH = "/permissions";

    //sheet hub resources
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String LOGOUT_PAGE = FULL_SERVER_PATH + "/logout";
    public final static String LOAD_SHEET = FULL_SERVER_PATH + HUB_SERVER_PATH + "/loadsheet";
    public final static String AVAILABLE_SHEETS = FULL_SERVER_PATH + HUB_SERVER_PATH + "/availablesheets";

    //sheethub general constants
    public final static int REFRESH_RATE = 500;

    //sheet view resources
    public final static String UPDATE_CELL = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/updatecell";
    public final static String LATEST_VERSION = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/latestversion";
    public final static String EVALUATE_ORIGINAL_VALUE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/evaluateoriginalvalue";
    public final static String SORT = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/sort";
    public final static String FILTER = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/filter";
    public final static String ADD_RANGE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/addrange";
    public final static String DELETE_RANGE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/deleterange";
    public final static String UPDATE_CELL_STYLE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/updatecellstyle";
    public final static String ADD_EMPTY_CELL = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/addemptycell";
    public final static String DYNAMIC_ANALYSIS_UPDATE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/dynamicanalysisupdate";
    public final static String SHEET_BY_VERSION = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/sheetbyversion";


    //permissions resources
    public final static String USER_PERMISSON_FOR_SHEET = FULL_SERVER_PATH + PERMISSION_PATH + "/userpermission";
    public final static String SHEET_PERMISSIONS = FULL_SERVER_PATH + PERMISSION_PATH + "/sheetpermissions";
    public final static String REQUEST_PERMISSION = FULL_SERVER_PATH + PERMISSION_PATH + "/requestpermission";
    public final static String PERMISSION_REQUEST_RESPONSE = FULL_SERVER_PATH + PERMISSION_PATH + "/requestresponse";
    public final static String SHEET_PERMISSIONS_REQUESTS = FULL_SERVER_PATH + PERMISSION_PATH + "/sheetpermissionsrequests";

}
