package com.aragh;

import com.aragh.model.Cart;
import com.aragh.model.Item;
import com.aragh.model.Product;
import com.aragh.promotion.BuyNItemsOfSKUForFixedPrice;
import com.aragh.promotion.BuyTwoSKUItemsForFixedPrice;
import com.aragh.promotion.PromotionOffer;
import com.aragh.promotion.engine.SimplePromotionEngine;
import com.aragh.promotion.store.InMemoryPromotionStore;
import com.aragh.promotion.store.PromotionStore;
import com.aragh.service.CartCheckoutService;
import com.aragh.service.SimpleCartCheckoutService;
import com.aragh.store.InMemoryProductStore;
import com.aragh.store.ProductStore;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a command line tool to test the scenarios.
 * Cart Checkout Service : {@link SimpleCartCheckoutService}
 * Promotion Engine : {@link SimplePromotionEngine}
 */
public class CartCheckoutMain {

    private static final Map<Pattern, Class<? extends PromotionOffer>> PROMOTION_MAP = new HashMap<>();
    private static final Pattern ITEM_PATTERN = Pattern.compile("(\\d)\\*(\\w)");

    //Ideally, I would have a builder/factory to define and parse the promotions
    static {
        PROMOTION_MAP.put(Pattern.compile("(\\d) of (\\w)'s for (\\d+(,\\d{1,2})?)"), BuyNItemsOfSKUForFixedPrice.class);
        PROMOTION_MAP.put(Pattern.compile("(\\w) & (\\w) for (\\d+(,\\d{1,2})?)"), BuyTwoSKUItemsForFixedPrice.class);
    }

    private final ProductStore productStore;
    private final PromotionStore promotionStore;
    private final CartCheckoutService cartCheckoutService;

    public CartCheckoutMain() {
        productStore = new InMemoryProductStore();
        promotionStore = new InMemoryPromotionStore();
        cartCheckoutService = new SimpleCartCheckoutService(new SimplePromotionEngine(promotionStore));
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            CartCheckoutMain cartCheckoutMain = new CartCheckoutMain();
            cartCheckoutMain.readProducts(scanner);
            cartCheckoutMain.readPromotionOffer(scanner);
            Cart cart = new Cart();
            cartCheckoutMain.readItems(cart, scanner);
            System.out.println("Totals : " + cartCheckoutMain.getTotals(cart));
        }
    }

    public BigDecimal getTotals(Cart cart) {
       return cartCheckoutService.getCartTotal(cart);
    }

    public void readItems(Cart cart, Scanner scanner) {
        System.out.println("Enter the number of items in cart");
        int numberOfItems = Integer.parseInt(scanner.nextLine());

        while (numberOfItems > 0) {
            System.out.println("Enter the item as quantity*item For Example 5*A");
            String scenario = scanner.nextLine();
            Matcher matcher = ITEM_PATTERN.matcher(scenario);
            while (matcher.find()) {
                int quantity = Integer.parseInt(matcher.group(1));
                Character sku = matcher.group(2).charAt(0);
                cart.add(Item.of(sku, quantity, productStore.findBySkuId(sku).orElseThrow().getUnitPrice()));
            }
            --numberOfItems;
        }
    }

    public void readPromotionOffer(Scanner scanner) {
        System.out.println("Enter the number of active promotions");
        int numberOfPromotions = Integer.parseInt(scanner.nextLine());

        while (numberOfPromotions > 0) {
            System.out.println("Enter the active promotions for each SKU like " +
                    "3 of A's for 130 or C & D for 30");

            String promotionOffer = scanner.nextLine();
            Map.Entry<Pattern, Class<? extends PromotionOffer>> matchEntry = PROMOTION_MAP.entrySet().stream()
                    .filter(entry -> promotionOffer.matches(entry.getKey().pattern()))
                    .findFirst().orElse(null);

            if (matchEntry == null) {
                System.out.println("Invalid promotion offer " + promotionOffer);
                System.exit(-1);
            }

            String promotionName = matchEntry.getValue().getSimpleName();
            switch (promotionName) {
                case "BuyNItemsOfSKUForFixedPrice":
                    Matcher matcher = matchEntry.getKey().matcher(promotionOffer);
                    while (matcher.find()) {
                        int quantity = Integer.parseInt(matcher.group(1));
                        Character sku = matcher.group(2).charAt(0);
                        BigDecimal price = BigDecimal.valueOf(Long.parseLong(matcher.group(3)));
                        promotionStore.save(new BuyNItemsOfSKUForFixedPrice(sku, quantity, price));
                    }
                    break;

                case "BuyTwoSKUItemsForFixedPrice":
                    matcher = matchEntry.getKey().matcher(promotionOffer);
                    while (matcher.find()) {
                        Character sku1 = matcher.group(1).charAt(0);
                        Character sku2 = matcher.group(2).charAt(0);
                        BigDecimal price = BigDecimal.valueOf(Long.parseLong(matcher.group(3)));
                        promotionStore.save(new BuyTwoSKUItemsForFixedPrice(sku1, sku2, price));
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Promotion " + promotionName + " not supported");
            }
            --numberOfPromotions;
        }
    }

    public void readProducts(Scanner scanner) {
        System.out.println("Enter the unit price for each SKU as space delimited, " +
                "and each product as comma separated. For Example : A 50,B 30,C 20");
        String[] products = scanner.nextLine().split(",");
        Arrays.stream(products).map(sku -> {
            String[] skuWithUnitPrice = sku.split("\\s+");
            if (skuWithUnitPrice.length != 2) {
                System.out.println("Invalid product SKU");
                System.exit(-1);
            }
            return Product.of(skuWithUnitPrice[0].charAt(0),
                    BigDecimal.valueOf(Long.parseLong(skuWithUnitPrice[1])));
        }).forEach(productStore::saveProduct);
    }
}
