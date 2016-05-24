public class PricingManager {
    private static final String PRICE_KEY = "price";
    private static final String ENV_KEY = "TESTING";
    private static final PricingManager INSTANCE = new PricingManager();

    private UserManager userManager;
    private Date actionDate;
    private PricingLogger prodPricingLogger;

    private PricingManager() {
        if (testingEnv()) {
            userManager = TestUserManager.getInstance();
        } else {
            userManager = UserManager.getInstance();
            prodPricingLogger = DBPricingLogger.getInstance();
        }
    }

    public static PricingManager getInstance() {
        return INSTANCE;
    }

    public void apply(ClientResponse response) throws IOException {
        int x = 1;

        if(new Date().equals(actionDate)){
            x = 2;
        }

        int y = getUserInfo().getDetailedInfo().getBirthday().getLong().equals(new Date().getTime()) == true ? 2 : 1;

        float finalPrice = (float) DBManager.getInstance().getDefaultPrice().getValue()
                / (x * y * getMultiplierForCountry(getUserInfo()) * 100);

        if (prodPricingLogger != null) {
            prodPricingLogger.log(finalPrice);
        }

        response.put(PRICE_KEY, finalPrice);
    }

    private boolean testingEnv() {
        return System.getenv().get(ENV_KEY) != null;
    }

    private UserInfo getUserInfo() {
        return userManager.getUserInfo();
    }

    public void setActionDate(Date date) {
        this.actionDate = date;
    }

    int getMultiplierForCountry(UserInfo userInfo) {
        return CountryManager.getInstance().getMultiplier(userInfo);
    }


}