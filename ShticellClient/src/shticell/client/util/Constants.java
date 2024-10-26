package shticell.client.util;

public class Constants {

    //global constants
    public final static int REFRESH_RATE = 500;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    //fxml locations
    public final static String SHEET_HUB_MAIN_PAGE_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/main/shticell-hub-main.fxml";
    public final static String SHEET_VIEW_MAIN_PAGE_FXML_RESOURCE_LOCATION = "/shticell/client/sheetpanel/main/shticellApp.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/shticell/client/sheethub/components/login/login-page.fxml";
    public final static String FORMULA_BUILDER_FXML_RESOURCE_LOCATION = "/shticell/client/sheetpanel/command/components/formulabuilder/formulabuilder.fxml";
    public final static String PERMISSION_REQUEST_RESOURCE_LOCATION = "/shticell/client/sheethub/components/commands/components/permissionrequest/request-permission.fxml";
    public final static String PERMISSION_RESPONSE_RESOURCE_LOCATION = "/shticell/client/sheethub/components/commands/components/permissionresponse/permission-response.fxml";
    public final static String CHAT_ROOM_RESOURCE_LOCATION = "/shticell/client/sheethub/components/commands/components/chat/chatroom/chat-room-main.fxml";

    //server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/ShticellServer";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    private final static String HUB_SERVER_PATH = "/sheethub";
    private final static String SHEET_VIEW_SERVER_PATH = "/sheetview";
    private final static String PERMISSION_PATH = "/permissions";
    private final static String CHAT_PATH = "/chat";


    //sheet hub resources
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String LOGOUT_PAGE = FULL_SERVER_PATH + "/logout";
    public final static String LOAD_SHEET = FULL_SERVER_PATH + HUB_SERVER_PATH + "/loadsheet";
    public final static String AVAILABLE_SHEETS = FULL_SERVER_PATH + HUB_SERVER_PATH + "/availablesheets";
    public final static String OWNED_SHEETS = FULL_SERVER_PATH + HUB_SERVER_PATH + "/ownedsheets";


    //sheet view resources
    public final static String UPDATE_CELL = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/updatecell";
    public final static String LATEST_VERSION = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/latestversion";
    public final static String EVALUATE_ORIGINAL_VALUE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/evaluateoriginalvalue";
    public final static String SORT = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/sort";
    public final static String FILTER = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/filter";
    public final static String ADD_RANGE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/addrange";
    public final static String DELETE_RANGE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/deleterange";
    public final static String UPDATE_CELLS_STYLE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/updatecellsstyle";
    public final static String ADD_EMPTY_CELLS = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/addemptycells";
    public final static String DYNAMIC_ANALYSIS_UPDATE = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/dynamicanalysisupdate";
    public final static String SHEET_BY_VERSION = FULL_SERVER_PATH + SHEET_VIEW_SERVER_PATH + "/sheetbyversion";


    //permissions resources
    public final static String USER_PERMISSON_FOR_SHEET = FULL_SERVER_PATH + PERMISSION_PATH + "/userpermission";
    public final static String SHEET_PERMISSIONS = FULL_SERVER_PATH + PERMISSION_PATH + "/sheetpermissions";
    public final static String REQUEST_PERMISSION = FULL_SERVER_PATH + PERMISSION_PATH + "/requestpermission";
    public final static String PERMISSION_RESPONSE = FULL_SERVER_PATH + PERMISSION_PATH + "/response";
    public final static String SHEET_PENDING_PERMISSIONS_REQUESTS = FULL_SERVER_PATH + PERMISSION_PATH + "/pendingrequests";


    //chat resources

    public final static String USERS_LIST = FULL_SERVER_PATH + CHAT_PATH + "/userslist";
    public final static String SEND_CHAT_LINE = FULL_SERVER_PATH + CHAT_PATH + "/sendChat";
    public final static String CHAT_LINES_LIST = FULL_SERVER_PATH + CHAT_PATH +  "/chatlines";


    //
    public final static String STYLES_FOLDER_LOCATION = "/shticell/client/sheetpanel/skinmanager/styles/";
    // Dimensions for the Hub page
    public static final double HUB_PAGE_WIDTH = 1000;
    public static final double HUB_PAGE_HEIGHT = 600;

    // Dimensions for the Login page
    public static final double LOGIN_PAGE_WIDTH = 330;
    public static final double LOGIN_PAGE_HEIGHT = 270;

    // Dimensions for the Sheet view page
    public static final double SHEET_VIEW_PAGE_WIDTH = 1200;
    public static final double SHEET_VIEW_PAGE_HEIGHT = 675;

    // Dimensions for Permission request popup
    public static final double PERMISSION_REQUEST_POPUP_WIDTH = 500;
    public static final double PERMISSION_REQUEST_POPUP_HEIGHT = 400;

    // Dimensions for Permission response popup
    public static final double PERMISSION_RESPONSE_POPUP_WIDTH = 700;
    public static final double PERMISSION_RESPONSE_POPUP_HEIGHT = 550;

    // Dimensions for Chat popup
    public static final double CHAT_POPUP_WIDTH = 1000;
    public static final double CHAT_POPUP_HEIGHT = 550;

    // Sheet grid pane
    public static final double SHEET_GRID_PANE_WIDTH = 800;
    public static final double SHEET_GRID_PANE_HEIGHT = 600;

    //alignment
    public final static String ALIGNMENT_LEFT = "-fx-alignment: center-left;";
    public final static String ALIGNMENT_RIGHT = "-fx-alignment: center-right;";
    public final static String ALIGNMENT_CENTER = "-fx-alignment: center;";

    // Styling
    public final static String TEXT_COLOR ="-fx-text-fill: ";
    public final static String BACKGROUND_COLOR ="-fx-background-color: ";

    // UI sheet model
    public final static int RGB_MAX_VALUE = 255;
    public final static int ROW_COL_INIT_CONSTRAINT = 30;
    public final static String DEPENDENCIES_HIGHLIGHT_COLOR = "lightblue";
    public final static String INFLUENCE_HIGHLIGHT_COLOR = "lightgreen";
    public final static String RANGE_HIGHLIGHT_STYLE = "-fx-border-color: blue; -fx-border-width: 1px; ";
}
