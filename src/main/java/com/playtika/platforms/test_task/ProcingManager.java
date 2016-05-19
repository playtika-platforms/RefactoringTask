

public class PricingManager {
    protected UserManager userManager;
    protected Date actionDate;
    protected PricingLogger prodPricingLogger;

    private PricingManager(){
        if(testingEnv()){
            userManager  = TestUserManager.getInstance();
        }else{
            userManager = UserManager.getInstance();
            prodPricingLogger = DBPricingLogger.getInstance();
        }

    }

    public void apply(ClientResponse response) throws IOException{
        int x = 1;

        if(actionDate.equals(new Date())){
            x = 2;
        }

        int y = userManager.getUserInfo().getDetailedInfo().getBirthday().getLong().equals(new Date()) == true? 2: 1;

        int finalPrice = DBManager.getInstance().getDefaultPrice().getValue() /( x * y * getMultiplierForCountry(userManager.getUserInfo()) * 100);

        if(prodPricingLogger != null){
            prodPricingLogger.log(finalPrice);
        }

        response.put("price",finalPrice);
    }

    protected boolean testingEnv() {
        return System.getenv().get("TESTING") != null;
    }

    public void setActionDate(Date date){
        this.date = date;
    }

    @VisibleForTesting
    private int getMultiplierForCountry(UserInfo userInfo){
        return CountryManager.getInstance().getMultiplier(userInfo);
    }


}