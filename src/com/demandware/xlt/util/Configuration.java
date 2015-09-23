package com.demandware.xlt.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.xceptance.common.util.PropertiesUtils;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Configuration for the Test
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class Configuration
{
    /** A list of search params which should result in a no hits page. */
    private final List<String> noHitSearchParams;

    /**
     * The name of the current running TestCase' class
     */
    private final String testClassName;

    /**
     * Minimum number of products to search
     */
    private final int maxSearchProducts;

    /**
     * Maximum number of products to search
     */
    private final int minSearchProducts;

    /**
     * Top category browsing probability
     */
    private final int topCategoryBrowsingProbability;

    /**
     * Probability for attribute refinements
     */
    private final int refineProbability;

    /**
     * Maximum number of attribute refinements
     */
    private final int maxRefinements;

    /**
     * Minimum number of attribute refinements
     */
    private final int minRefinements;

    /**
     * Probability for category refinements
     */
    private final int categoryRefinementProbability;

    /**
     * Probability for sorting
     */
    private final int sortingProbability;

    /**
     * Probability for paging
     */
    private final int pagingProbability;

    /**
     * Minimum number of pagings
     */
    private final int minPagings;

    /**
     * Maximum number of pagings
     */
    private final int maxPagings;

    /**
     * Probability for accessing a product via quick view
     */
    private final int quickViewProbability;

    /**
     * Minimum number of products to browse
     */
    private final int minBrowsingProducts;

    /**
     * Maximum number of products to browse
     */
    private final int maxBrowsingProducts;

    /**
     * Special setup for more sophisticated probability handling
     */
    private final String productDistributionData;

    /**
     * Holds the distribution to pick from
     */
    private int[] productDistribution;

    /**
     * Probability to execute a 'no-hits' search
     */
    private final int searchNoHitsProbability;

    /**
     * Probability to change the items per page
     */
    private final int itemsPerPageProbability;

    /**
     * Whether or not to make the analytics calls
     */
    private final boolean loadAnalytics;

    /**
     * Whether or not to make the Resources-Load calls
     */
    private final boolean loadAppResources;

    /**
     * Whether or not to load search suggestions
     */
    private final boolean searchSuggestionsEnabled;

    /**
     * Whether or not to separate account pools
     */
    private final boolean isAccountPoolSiteSeparated;

    /**
     * The predefined country code
     */
    private final String countryCode;

    /**
     * The predefined state code
     */
    private final String stateCode;

    /**
     * The predefined zip code
     */
    private final String zipCode;

    /**
     * The predefined phone number
     */
    private final String phone;

    /**
     * The predefined street
     */
    private final String street;

    /**
     * The predefined town
     */
    private final String town;

    /**
     * The predefined country code
     */
    private final String countryCode2;

    /**
     * The predefined state code
     */
    private final String stateCode2;

    /**
     * The predefined zip code
     */
    private final String zipCode2;

    /**
     * The predefined phone number
     */
    private final String phone2;

    /**
     * The predefined street
     */
    private final String street2;

    /**
     * The predefined town
     */
    private final String town2;

    /**
     * Indicates whether a refinement by brands should be done or not
     */
    private final boolean refineByBrand;

    /**
     * Minimum number of products to add to cart. Only used if no product distribution is configured
     */
    private final int minAddToCartProducts;

    /**
     * Maximum number of products to add to cart. Only used if no product distribution is configured
     */
    private final int maxAddToCartProducts;

    /**
     * The current language of the page under test
     */
    private final String language;

    private final int paypalProbability;

    private final int paymentsBlockedAbove;

    private final int paymentsBlockedBelow;

    private final boolean useInfiniteScroll;

    private final Site site;

    private final int minStoreSearches;

    private final int maxStoreSearches;

    private final List<String> storeSearchRadii;

    private final List<String> storesAvailableIn;
    
    private final List<String> storeZipcodes;

    public Configuration(final String testClassName, final Site site)
    {
        this.testClassName = testClassName;

        this.site = site;

        // search
        this.searchNoHitsProbability = getProperty("search.noHitsProbability", 0);
        this.searchSuggestionsEnabled = getProperty("search.loadSuggestions", true);
        this.maxSearchProducts = getProperty("search.products.max", getProperty("products.max", 1));
        this.minSearchProducts = getProperty("search.products.min", getProperty("products.min", 1));
        this.noHitSearchParams = createList(getProperty("search.noHitsValues"));

        // browsing
        this.refineByBrand = getProperty("browsing.refinement.byBrand", true);
        this.topCategoryBrowsingProbability = getProperty("browsing.topCategoryProbability", 100);
        this.categoryRefinementProbability = getProperty("browsing.categoryRefinementProbability", 0);
        this.refineProbability = getProperty("browsing.refinement.probability", 0);
        this.maxRefinements = getProperty("browsing.refinement.max", 0);
        this.minRefinements = getProperty("browsing.refinement.min", 0);
        this.pagingProbability = getProperty("browsing.paging.probability", 0);
        this.maxPagings = getProperty("browsing.paging.max", 0);
        this.minPagings = getProperty("browsing.paging.min", 0);
        this.useInfiniteScroll = getProperty("browsing.infinitescroll", false);
        this.sortingProbability = getProperty("browsing.sortProbability", 0);
        this.itemsPerPageProbability = getProperty("browsing.changeItemsPerPageProbability", 0);
        this.quickViewProbability = getProperty("browsing.quickviewProbability", 0);
        this.maxBrowsingProducts = getProperty("browsing.products.max", getProperty("products.max", 1));
        this.minBrowsingProducts = getProperty("browsing.products.min", getProperty("products.min", 1));

        // addToCart
        this.maxAddToCartProducts = getProperty("addToCart.products.max", getProperty("products.max", 1));
        this.minAddToCartProducts = getProperty("addToCart.products.min", getProperty("products.min", 1));
        this.productDistributionData = getProperty("addToCart.products.distribution");
        if (this.productDistributionData != null)
        {
            setupDistributionData();
        }
        // address
        // NOTE: non configured values are generated directly in the AddressImpl.java
        this.street = getProperty("address.1.street");
        this.town = getProperty("address.1.town");
        this.zipCode = getProperty("address.1.zipcode");
        this.countryCode = getProperty("address.1.countrycode");
        this.stateCode = getProperty("address.1.statecode");
        this.phone = getProperty("address.1.phone");

        // secondary address
        this.street2 = getProperty("address.2.street");
        this.town2 = getProperty("address.2.town");
        this.zipCode2 = getProperty("address.2.zipcode");
        this.countryCode2 = getProperty("address.2.countrycode");
        this.stateCode2 = getProperty("address.2.statecode");
        this.phone2 = getProperty("address.2.phone");

        // payment
        this.paypalProbability = getProperty("payment.paypal.probability", 0);

        // other
        this.loadAnalytics = getProperty("load.analytics", false);
        this.loadAppResources = getProperty("load.resources-load", false);
        this.isAccountPoolSiteSeparated = getProperty("account.pool.siteSeparated", false);
        this.language = getProperty("test.language");

        // order total limits
        final int lowerLimit = getProperty("test.paymentBlocked.above", -1);
        final int upperLimit = getProperty("test.paymentBlocked.below", -1);
        if (lowerLimit < 0 && upperLimit < 0)
        {
            this.paymentsBlockedAbove = Integer.MAX_VALUE;
            this.paymentsBlockedBelow = -1;
        }
        else
        {
            this.paymentsBlockedAbove = (lowerLimit >= 0) ? lowerLimit : -1;
            this.paymentsBlockedBelow = (upperLimit >= 0) ? upperLimit : Integer.MAX_VALUE;
        }

        // Store locator
        this.minStoreSearches = getProperty("storeSearch.min", 0);
        this.maxStoreSearches = getProperty("storeSearch.max", 0);

        this.storeSearchRadii = getSplittedPropertyList("storeSearch.radii", " ");
        this.storesAvailableIn = getSplittedPropertyList("storeSearch.storeLocations", " ");
        this.storeZipcodes = getSplittedPropertyList("storeSearch.zipcodes", " ");

        Assert.assertNotNull("Property test.language have to be set", this.language);
    }

    /**
     * Returns the effective key to be used for property lookup via one of the getProperty(...) methods.
     * <p>
     * This method implements the fall-back logic:
     * <ol>
     * <li>user name plus simple key (e.g. <b>TMyRunningTest.</b>password)</li>
     * <li>site prefix plus user name plus simple key (e.g. <b>de.TMyRunningTest.</b>password)</li>
     * <li>test class name plus simple key (e.g. <b>com.xceptance.xlt.samples.testsuite.tests.TAuthor.</b>password)</li>
     * <li>site prefix plus plus simple key (e.g. <b>de.</b>password)</li>
     * <li>simple key (e.g. password)</li>
     * </ol>
     * 
     * @param bareKey
     *            the bare property key, i.e. without any prefixes
     * @param sitePrefix
     *            site specific property prefix (if any)
     * @return the first key that produces a result
     */
    private String getEffectiveKey(final String bareKey, final String sitePrefix)
    {
        String effectiveKey = null;
        final XltProperties xltProperties = XltProperties.getInstance();

        // 1. use the test class' simple name as prefix
        // TBrowse.<property> (matches all *.TBrowse)
        // final String userNameQualifiedKey = this.testCaseSimpleName + "." + bareKey;
        final String userNameQualifiedKey = Session.getCurrent().getUserName() + "." + bareKey;
        if (xltProperties.containsKey(userNameQualifiedKey))
        {
            effectiveKey = userNameQualifiedKey;
        }
        else
        {
            // 2. use site specific prefix and test case simple class name as property prefix
            // e.g. <site>.TBrowse.<property> (matches all TBrowse in context of <site>)
            final String prefixedClassKey = sitePrefix + "." + testClassName + "." + bareKey;
            if (xltProperties.containsKey(prefixedClassKey))
            {
                effectiveKey = prefixedClassKey;
            }
            else
            {
                // 3. use the test class name as prefix since this is the most specific property
                // e.g. com.xlt.project.test.TBrowse.property (matches this specific class only)
                final String classNameQualifiedKey = this.testClassName + "." + bareKey;
                if (xltProperties.containsKey(classNameQualifiedKey))
                {
                    effectiveKey = classNameQualifiedKey;
                }
                else
                {
                    // 4. use the given site prefix and the bare key
                    // e.g. <site>.<property> (matches all tests in context of <site>)
                    final String prefixedBareKey = sitePrefix + bareKey;
                    if (xltProperties.containsKey(prefixedBareKey))
                    {
                        effectiveKey = prefixedBareKey;
                    }
                    else
                    {
                        // 5. use bare key (most general property notation)
                        // just <property>
                        effectiveKey = bareKey;
                    }
                }
            }
        }

        return effectiveKey;
    }

    /**
     * Returns the effective key to be used for property lookup via one of the getProperty(...) methods.
     * <p>
     * This method implements the fall-back logic:
     * <ol>
     * <li>user name plus simple key (e.g. <b>TMyRunningTest.</b>password)</li>
     * <li>site prefix plus user name plus simple key (e.g. <b>de.TMyRunningTest.</b>password)</li>
     * <li>test class name plus simple key (e.g. <b>com.xceptance.xlt.samples.testsuite.tests.TAuthor.</b>password)</li>
     * <li>site prefix plus plus simple key (e.g. <b>de.</b>password)</li>
     * <li>simple key (e.g. password)</li>
     * </ol>
     * As site the currently configured site is taken.
     * 
     * @param bareKey
     *            the bare property key, i.e. without any prefixes
     * @return the first key that produces a result
     */
    protected String getEffectiveKey(final String bareKey)
    {
        return getEffectiveKey(bareKey, site.getPropertyPrefix());
    }

    /**
     * Get the highest number of attribute refinements.
     * 
     * @return highest number of attribute refinements
     */
    public final int getMaxRefinements()
    {
        return maxRefinements;
    }

    /**
     * Get the lowest number of refinements.
     * 
     * @return lowest number of refinements
     */
    public final int getMinRefinements()
    {
        return minRefinements;
    }

    /**
     * Get the probability for refining the results by their attributes.
     * 
     * @return probability for refining the results
     */
    public final int getRefineProbability()
    {
        return refineProbability;
    }

    /**
     * Get the probability of starting the browsing at a top category. This implies that otherwise browsing starts at a
     * sub category.
     * 
     * @return probability of starting the browsing at a top category
     */
    public int getTopCategoryBrowsingProbability()
    {
        return topCategoryBrowsingProbability;
    }

    /**
     * Get the probability to refine results by category.
     * 
     * @return probability to refine results by category
     */
    public final int getCategoryRefinementProbability()
    {
        return categoryRefinementProbability;
    }

    /**
     * Get the probability to sort the results.
     * 
     * @return probability to sort the results
     */
    public final int getSortingProbability()
    {
        return sortingProbability;
    }

    /**
     * Get the probability for paging through the results.
     * 
     * @return probability for paging
     */
    public final int getPagingProbability()
    {
        return pagingProbability;
    }

    /**
     * Get the lowest number of pagings.
     * 
     * @return lowest number of pagings
     */
    public final int getMinPagings()
    {
        return minPagings;
    }

    /**
     * Get the highest number of pagings.
     * 
     * @return highest number of pagings
     */
    public final int getMaxPagings()
    {
        return maxPagings;
    }

    /**
     * Get the probability for accessing a product via its quick view. This implies that the product is accessed via its
     * product detail page.
     * 
     * @return probability for accessing a product by its quick view
     */
    public final int getQuickViewProbability()
    {
        return quickViewProbability;
    }

    /**
     * Get the 'no-hits' probability for searches. This implies that a search has to end in at least one hit otherwise.
     * 
     * @return 'no-hits' probability for searches
     */
    public int getSearchNoHitsProbability()
    {
        return searchNoHitsProbability;
    }

    /**
     * Get the probability to change the number of items per page.
     * 
     * @return probability to change the number of items per page
     */
    public int getItemsPerPageProbability()
    {
        return itemsPerPageProbability;
    }

    /**
     * Is the analytics call desired?
     * 
     * @return <code>true</code> if the analytics call is desired, <code>false</code> otherwise.
     */
    public boolean loadAnalytics()
    {
        return loadAnalytics;
    }

    /**
     * Is the Resources-Load call desired?
     * 
     * @return <code>true</code> if the Resources-Load call is desired, <code>false</code> otherwise.
     */
    public boolean loadAppResources()
    {
        return loadAppResources;
    }

    /**
     * Load search suggestions?
     * 
     * @return <code>true</code> if to load search suggestions, <code>false</code> otherwise.
     */
    public boolean loadSearchSuggestions()
    {
        return searchSuggestionsEnabled;
    }

    /**
     * Returns the number of products to deal with.
     * 
     * @return the number of products to visits/see/put in to the cart
     */
    public int getNumberOfProductsForAddToCart()
    {
        if (productDistributionData != null)
        {
            return productDistribution[XltRandom.nextInt(productDistribution.length)];
        }
        else
        {
            return XltRandom.nextInt(getMinAddToCartProducts(), getMaxAddToCartProducts());
        }
    }

    /**
     * Get the highest number of products to add to cart.
     * 
     * @return highest number of products to add to cart
     */
    public int getMaxAddToCartProducts()
    {
        return maxAddToCartProducts;
    }

    /**
     * Get the lowest number of products to add to cart.
     * 
     * @return lowest number of products to add to cart
     */
    public int getMinAddToCartProducts()
    {
        return minAddToCartProducts;
    }

    /**
     * Sets up the product distribution, if desired.
     */
    private void setupDistributionData()
    {
        if (productDistributionData == null)
        {
            // Product distribution is not desired at all
            return;
        }

        // Internal helper class
        class Pair
        {
            public Pair(final int value, final int amount)
            {
                super();
                this.value = value;
                this.amount = amount;
            }

            int value;

            int amount;
        }

        // Initialize buckets
        final List<Pair> buckets = new ArrayList<Pair>();

        // Format is 1/12 2/34 40/2
        // So token is a whitespace.
        final StringTokenizer st = new StringTokenizer(productDistributionData, " ");
        while (st.hasMoreTokens())
        {
            final String token = st.nextToken();
            final String[] splitString = token.split("/");
            if (splitString.length == 1)
            {
                // Just a 2 3 4 or something like that, it means that number into the next bucket
                buckets.add(new Pair(buckets.size() + 1, new Integer(Integer.valueOf(splitString[0]))));
            }
            else if (splitString.length > 1)
            {
                final int value = Integer.valueOf(splitString[0]);
                final int amount = Integer.valueOf(splitString[1]);
                buckets.add(new Pair(value, amount));
            }
        }

        if (buckets.isEmpty())
        {
            // Still no list.
            return;
        }

        // Build the lookup array and count first to have its future size handy
        int total = 0;
        for (final Pair pair : buckets)
        {
            total = total + pair.amount;
        }

        if (total == 0)
        {
            return;
        }

        // Create the array of integers
        final int[] distributionList = new int[total];

        int pos = 0;
        for (final Pair pair : buckets)
        {
            for (int i = 0; i < pair.amount; i++)
            {
                distributionList[pos] = pair.value;
                pos++;
            }
        }

        this.productDistribution = distributionList;
    }

    /**
     * Do we use site separated account pools.
     * 
     * @return <code>true</code> if separated account pools are needed, <code>false</code> otherwise.
     */
    public boolean isAccountPoolSiteSeparated()
    {
        return isAccountPoolSiteSeparated;
    }

    /**
     * The start URL.
     * 
     * @return the start URL
     */
    public String getStartURL()
    {
        return getProperty("start.url");
    }

    /**
     * Returns the value for the given key as configured in the test suite configuration. See
     * {@link #getProperty(String)} for a description of the look-up logic. This method returns the passed default value
     * if the property value could not be found.
     * 
     * @param key
     *            the property key
     * @param defaultValue
     *            the default value
     * @return the property value as an int
     */
    public int getProperty(final String key, final int defaultValue)
    {
        final String effectiveKey = getEffectiveKey(key);

        return XltProperties.getInstance().getProperty(effectiveKey, defaultValue);
    }

    /**
     * Returns the value for the given key as configured in the test suite configuration. See
     * {@link #getProperty(String)} for a description of the look-up logic. This method returns the passed default value
     * if the property value could not be found.
     * 
     * @param key
     *            the property key
     * @param defaultValue
     *            the default value
     * @return the property value
     */
    public String getProperty(final String key, final String defaultValue)
    {
        final String effectiveKey = getEffectiveKey(key);

        return XltProperties.getInstance().getProperty(effectiveKey, defaultValue);
    }

    /**
     * Returns the value for the given key as configured in the test suite configuration. See
     * {@link #getProperty(String)} for a description of the look-up logic. This method returns the passed default value
     * if the property value could not be found.
     * 
     * @param key
     *            the property key
     * @param defaultValue
     *            the default value
     * @return the property value as a boolean
     */
    public boolean getProperty(final String key, final boolean defaultValue)
    {
        final String effectiveKey = getEffectiveKey(key);

        return XltProperties.getInstance().getProperty(effectiveKey, defaultValue);
    }

    /**
     * Returns the value for the given key as configured in the test suite configuration. The process of looking up a
     * property uses multiple fall-backs. When resolving the value for the key "password", for example, the following
     * effective keys are tried, in this order:
     * <ol>
     * <li>the test user name plus simple key, e.g. "TAuthor.password"</li>
     * <li>the test class name plus simple key, e.g. "com.xceptance.xlt.samples.tests.TAuthor.password"</li>
     * <li>the simple key, e.g. "password"</li>
     * </ol>
     * This multi-step hierarchy allows for test-user-specific or test-case-specific overrides of certain settings,
     * while falling back to the globally defined values if such specific settings are absent.
     * 
     * @param key
     *            the simple property key
     * @return the property value, or <code>null</code> if not found
     */
    public String getProperty(final String key)
    {
        final String effectiveKey = getEffectiveKey(key);

        return XltProperties.getInstance().getProperty(effectiveKey);
    }

    /**
     * Returns all properties for this domain key, strips the key from the property name, e.g.
     * ClassName.Testproperty=ABC --> TestProperty=ABC Attention: Properties without a domain (e.g. foobar=test) or
     * domain only properties are invalid and will be ignored. A property has to have at least this form:
     * domain.propertyname=value
     * 
     * @param domain
     *            domain for the properties
     * @return map with all key value pairs of properties
     */
    public Map<String, String> getPropertiesForKey(final String domain)
    {
        return PropertiesUtils.getPropertiesForKey(getEffectiveKey(domain), XltProperties.getInstance().getProperties());
    }

    /**
     * Gets a random number of products to search between search.products.min and browsing.products.max.
     * 
     * @return a random number of products to search between search.products.min and browsing.products.max
     */
    public int getRandomNumberOfSearchProducts()
    {
        return XltRandom.nextInt(this.getMinSearchProducts(), this.getMaxSearchProducts());
    }

    /**
     * Get the highest number of products to search.
     * 
     * @return highest number of products to search
     */
    public int getMaxSearchProducts()
    {
        return maxSearchProducts;
    }

    /**
     * Get the lowest number of products to search.
     * 
     * @return lowest number of products to search
     */
    public int getMinSearchProducts()
    {
        return minSearchProducts;
    }

    /**
     * Gets a random number of products to browse between browsing.products.min and browsing.products.max.
     * 
     * @return a random number of products to browse between browsing.products.min and browsing.products.max
     */
    public int getRandomNumberOfBrowsingProducts()
    {
        return XltRandom.nextInt(this.getMinBrowsingProducts(), this.getMaxBrowsingProducts());
    }

    /**
     * Get the highest number of products to browse.
     * 
     * @return highest number of products to browse
     */
    public int getMaxBrowsingProducts()
    {
        return maxBrowsingProducts;
    }

    /**
     * Get the lowest number of products to browse.
     * 
     * @return lowest number of products to browse
     */
    public int getMinBrowsingProducts()
    {
        return minBrowsingProducts;
    }

    /**
     * Returns a random number between browsing.paging.min and browsing.paging.max.
     * 
     * @returna random number between browsing.paging.min and browsing.paging.max
     */
    public int getRandomPagingCount()
    {
        return XltRandom.nextInt(getMinPagings(), getMaxPagings());
    }

    /**
     * Returns a random number between browsing.refinement.min and browsing.refinement.max.
     * 
     * @returna random number between browsing.refinement.min and browsing.refinement.max
     */
    public int getRandomRefinementCount()
    {
        return XltRandom.nextInt(getMinRefinements(), getMaxRefinements());
    }

    /**
     * Returns whether the refinement by brand is enabled or not.
     * 
     * @return whether the refinement by brand is enabled or not.
     */
    public boolean isRefineByBrandEnabled()
    {
        return refineByBrand;
    }

    public int getPaypalProbability()
    {
        return paypalProbability;
    }

    /**
     * Get the town.
     * 
     * @return town
     */
    public String getTown()
    {
        return this.town;
    }

    /**
     * Get the house number and street.
     * 
     * @return house number and street
     */
    public String getStreet()
    {
        return this.street;
    }

    /**
     * Get the phone number.
     * 
     * @return phone number
     */
    public String getPhone()
    {
        return this.phone;
    }

    /**
     * Get the ZIP code.
     * 
     * @return ZIP code
     */
    public String getZipCode()
    {
        return this.zipCode;
    }

    /**
     * Get the state code (for example 'MA').
     * 
     * @return state code
     */
    public String getStateCode()
    {
        return this.stateCode;
    }

    /**
     * Get the country code (for example 'US').
     * 
     * @return country code
     */
    public String getCountryCode()
    {
        return this.countryCode;
    }

    /**
     * Get the town.
     * 
     * @return town
     */
    public String getTown2()
    {
        return this.town2;
    }

    /**
     * Get the house number and street.
     * 
     * @return house number and street
     */
    public String getStreet2()
    {
        return this.street2;
    }

    /**
     * Get the phone number.
     * 
     * @return phone number
     */
    public String getPhone2()
    {
        return this.phone2;
    }

    /**
     * Get the ZIP code.
     * 
     * @return ZIP code
     */
    public String getZipCode2()
    {
        return this.zipCode2;
    }

    /**
     * Get the state code (for example 'MA').
     * 
     * @return state code
     */
    public String getStateCode2()
    {
        return this.stateCode2;
    }

    /**
     * Get the country code (for example 'US').
     * 
     * @return country code
     */
    public String getCountryCode2()
    {
        return this.countryCode2;
    }

    /**
     * Get the text representation for the given key from the properties file in the configured language.
     * 
     * @param key
     *            the given text part key
     * @return the text representation for the given key in the configured language
     */
    public String getLanguageProperty(String key)
    {
        return getProperty("test.language." + getLanguage() + "." + key);
    }

    /**
     * Get the configured language for this TestCase, e.g en-US.
     * 
     * @return the configured language for this TestCase
     */
    private String getLanguage()
    {
        return this.language;
    }

    /**
     * Creates a list from an inserted string which is separated by ";"
     * 
     * @param property
     *            Property to split
     * @return list with separated elements
     */
    private List<String> createList(final String property)
    {
        if (property == null)
        {
            return null;
        }

        final String[] array = property.split(";");
        final List<String> list = new LoopingIterableList<String>();
        ((LoopingIterableList<String>) list).addAll(array);

        return list;
    }

    /**
     * Get a search phrase which will lead to the no results page, either taken from the property file or a random
     * string.
     * 
     * @return search phrase which will lead to the no results page likely
     */
    public String getNoHitSearchParam()
    {
        return noHitSearchParams == null ? RandomStringUtils.randomAlphabetic(XltRandom.nextInt(5, 10)) + " "
                                           + RandomStringUtils.randomAlphabetic(XltRandom.nextInt(8, 10)) : noHitSearchParams.iterator()
                                                                                                                             .next();
    }

    /**
     * Get the upper limit of blocked payment value.
     * 
     * @return the upper limit of blocked payment value
     */
    public int getPaymentsBlockedBelow()
    {
        return paymentsBlockedBelow;
    }

    /**
     * Get the lower limit of blocked payment value.
     * 
     * @return the lower limit of blocked payment value
     */
    public int getPaymentsBlockedAbove()
    {
        return paymentsBlockedAbove;
    }

    /**
     * List implementation which returns the same iterator which will loop through the list.
     * 
     * @author Bernd Weigel
     * @param <E>
     *            List element type
     */
    private class LoopingIterableList<E> extends ArrayList<E>
    {
        private static final long serialVersionUID = -8145812644295190028L;

        private Iterator<E> iterator;

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<E> iterator()
        {
            // save the iterator
            if (this.iterator == null || !iterator.hasNext())
            {
                iterator = super.iterator();
            }
            return iterator;
        }

        public void addAll(final E[] array)
        {
            for (int i = 0; i < array.length; i++)
            {
                this.add(array[i]);
            }
        }
    }

    /**
     * Indicates if infinite scroll should be used (instead of paging)
     * 
     * @return
     */
    public boolean useInfiniteScroll()
    {
        return this.useInfiniteScroll;
    }

    /**
     * Indicates if paging should be used (instead of infinite scroll)
     * 
     * @return
     */
    public boolean usePaging()
    {
        return !this.useInfiniteScroll;
    }

    /**
     * Get the lowest number of search runs
     * 
     * @return lowest number of searches
     */
    public final int getMinNumberOfStoreSearches()
    {
        return minStoreSearches;
    }

    /**
     * Get the highest number of search runs
     * 
     * @return highest number of searches
     */
    public final int getMaxNumberOfStoreSearches()
    {
        return maxStoreSearches;
    }

    /**
     * Returns a random number between browsing.paging.min and browsing.paging.max.
     * 
     * @return random number between browsing.paging.min and browsing.paging.max
     */
    public int getRandomNumberOfStoreSearches()
    {
        return XltRandom.nextInt(getMinNumberOfStoreSearches(), getMaxNumberOfStoreSearches());
    }

    /**
     * Returns a list of available radii
     * 
     * @return a list of radii
     */
    public List<String> getStoreSearchRadii()
    {
        return storeSearchRadii;
    }

    /**
     * Returns a list of country codes, which guarantee a positive result (hit) when performing a store locator search
     * 
     * @return a list of country codes
     */
    public List<String> getAvailableCountryCodesForStores()
    {
        return storesAvailableIn;
    }

    /**
     * This function can be used to split a property which contains multiple values in one string, i.e., the string
     * consists of words which are always separated by the same delimiter. It splits the content of the string at a
     * given delimiter and returns a list with the extracted words.
     * 
     * @param propertyName
     *            the name of the property
     * @param separator
     *            the character-sequence which separates the words in the string
     * @return an <code>ArrayList</code> containing the extracted words (if some values were provided), otherwise the
     *         list remains empty
     */
    public List<String> getSplittedPropertyList(final String propertyName, final String separator)
    {
        return getSplittedPropertyList(propertyName, separator, null);
    }

    /**
     * This function can be used to split a property which contains multiple values in one string, i.e., the string
     * consists of words which are always separated by the same delimiter. It splits the content of the string at a
     * given delimiter and returns a list with the extracted words.
     * 
     * @param propertyName
     *            the name of the property
     * @param separator
     *            the character-sequence which separates the words in the string
     * @param defaultPropertyValue
     *            default property value taken if the regular property value is not available (<code>null</code>)
     * @return an <code>ArrayList</code> containing the extracted words (if some values were provided), otherwise the
     *         list remains empty
     */
    public List<String> getSplittedPropertyList(final String propertyName, final String separator, final String defaultPropertyValue)
    {
        // The string propertyName is not allowed to be null or empty.
        if (StringUtils.isBlank(propertyName))
        {
            throw new IllegalArgumentException("propertyName must not be blank.");
        }
        
        // The separator may be blank (" "), so we only need to make sure it is not empty.
        if (StringUtils.isEmpty(separator))
        {
            throw new IllegalArgumentException("separator must not be empty.");
        }

        // Try to read the property.
        final String propertyValue = getProperty(propertyName, defaultPropertyValue);

        // Did we find the property and was a non-empty String returned?
        if (StringUtils.isBlank(propertyValue))
        {
            return new ArrayList<>();
        }

        // Split property into separate elements.
        final String[] values = StringUtils.split(propertyValue, separator);

        return new ArrayList<String>(Arrays.asList(values));
    }
    
    /**
     * Returns a random element from the given list. If the list is <code>null</code> or empty <code>null</code> is
     * returned.
     * 
     * @param list
     *            a list with Strings from which one is randomly chosen
     * @return on success a random element from the list otherwise <code>null</code>
     */
    private String getRandomElement(List<String> list)
    {
        if (list == null || list.isEmpty())
        {
            return null;
        }

        return list.get(XltRandom.nextInt(list.size()));
    }
    
    /**
     * Returns a randomly chosen zipcode from the property <i>"storeLocator.storeZipcodes"</i>
     * 
     * @return a random zipcode, or <code>null</code> if none was provided
     */
    public String getRandomZipCode()
    {
        return getRandomElement(storeZipcodes);
    }
}

