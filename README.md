# promotion-engine
A simple in-memory promotion engine for cart checkout process
The application core includes  
- **[CartCheckoutService](src/main/java/com/aragh/service/SimpleCartCheckoutService.java)** : The cart checkout service has an API to calculate the total price of the cart  
The total price is calculated after applying any applicable promotions. 
See test [SimpleCartCheckoutServiceTest](src/test/java/com/aragh/service/SimpleCartCheckoutServiceTest.java)
- **[PromotionEngine](src/main/java/com/aragh/promotion/engine/SimplePromotionEngine.java)** : Main responsibility of the promotion engine is to check if the promotion   
is active and whether the promotion can be applied on the items. The SimplePromotionEngine also filters 
out the items which have been applied promotion.
See test [SimplePromotionEngineTest](src/test/java/com/aragh/promotion/SimplePromotionEngineTest.java)
- **[BuyNItemsOfSKUForFixedPrice](src/main/java/com/aragh/promotion/BuyNItemsOfSKUForFixedPrice.java)** : The promotion offer that is applied on the SKU for the N Items.
See test [BuyNItemsOfSKUForFixedPriceTest](src/test/java/com/aragh/promotion/BuyNItemsOfSKUForFixedPriceTest.java)
- **[BuyTwoSKUItemsForFixedPrice](src/main/java/com/aragh/promotion/BuyTwoSKUItemsForFixedPrice.java)** : Promotion offer that is applied for two SKUs in the cart.  
See test [BuyTwoSKUItemsForFixedPrice](src/test/java/com/aragh/promotion/BuyTwoSKUItemsForFixedPriceTest.java)  


**Note and Considerations**:  
- More promotion types can be added by implementing the PromotionOffer.  
- The promotions are mutually exclusive and its assumed that if one promotion 
is applied on one sku then others are ignored. case 2 => either 2A = 30 or A=A40%
- The problem statement mentions that the cart has a list of single character sku, the Item
class has an skuId of type Character. However, this could be updated to use String/Generic type <T>.
- Basic validation exists for quantity, skuId and price and while creating and applying the promotion.
- Logging and Exception handling is basic and there is scope for improvement
- There is no Dependency injection used in the tests or the CartCheckoutMain. This is also a good consideration
for improvement
- PMD run locally to check cyclomatic complexity. I have not included
the maven maven-pmd-plugin as the build time can increase due to dependencies download.
The following ruleSets were applied
````
<ruleset>/rulesets/java/codesize.xml</ruleset>
<ruleset>/rulesets/java/basic.xml</ruleset>
<ruleset>/rulesets/java/design.xml</ruleset>
<ruleset>/rulesets/java/junit.xml</ruleset>
````

### Usage
This is a Java (Version: 11) and maven project and comes with maven wrapper  
To build the project, Clone and Navigate to the downloaded directory and run the command from terminal
> ./mvnw clean package

Once the project is built, navigate to the target directory and execute the jar
>  java -jar target/promotion-engine-1.0.jar

#### Sample input for commandLine run

Provide the input as shown below. The promotions can be provided as text. See example below.  
Please note that the input is case-sensitive. 

The below cart scenario is where the total is 280  
3 * A     130  
5 * B     45+45+30  
1 * C     -  
1 * D     30  
````
Enter the unit price for each SKU as space delimited, and each product as comma separated. For Example : A 50,B 30,C 20  
A 50,B 30,C 20,D 15  
Enter the number of active promotions
3  
Enter the active promotions for each SKU like 3 of A's for 130 or C & D for 30  
3 of A's for 130  
Enter the active promotions for each SKU like 3 of A's for 130 or C & D for 30    
2 of B's for 45  
Enter the active promotions for each SKU like 3 of A's for 130 or C & D for 30   
C & D for 30  
Enter the number of items in cart    
4  
Enter the item as quantity*item For Example 5*A   
3*A  
Enter the item as quantity*item For Example 5*A   
5*B  
Enter the item as quantity*item For Example 5*A    
1*C  
Enter the item as quantity*item For Example 5*A  
1*D  
Totals : 280  
````