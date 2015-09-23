package com.xceptance.xlt.api.util.elementLookup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.helpers.AttributesImpl;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.InputElementFactory;

/**
 * Test the implementation of {@link HPU}.
 * 
 * @author Matthias Ullrich (Xceptance Software Technologies GmbH)
 */
public class HPUTest
{
    /*
     * Find
     */

    /**
     * Create unasserted finder.
     */
    @Test
    public void testFind()
    {
        HPU.find();
    }

    /**
     * Use assertion shortcut without custom assertion message.
     */
    @Test
    public void testFindAsserted()
    {
        HPU.findAsserted();
    }

    /**
     * Use assertion shortcut with custom assertion message.
     */
    @Test
    public void testFindAssertedMsg()
    {
        HPU.findAsserted("message");
    }

    /**
     * Use assertion shortcut with empty custom assertion message.
     */
    @Test
    public void testFindAssertedMsgEmpty()
    {
        HPU.findAsserted("");
    }

    /**
     * Use assertion shortcut custom assertion message set to <code>null</code>.
     */
    @Test
    public void testFindAssertedMsgNull()
    {
        HPU.findAsserted(null);
    }

    /*
     * In
     */

    /**
     * Set an {@link HtmlPage} as lookup base.
     */
    @Test
    public void testFindInPage() throws Throwable
    {
        HPU.find().in(getHtmlPage(""));
    }

    /**
     * Set an {@link HtmlPage} as lookup base. In case of <code>null</code> an exception is expected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindInPageNull() throws Throwable
    {
        HPU.find().in((HtmlPage) null);
    }

    /**
     * Set an {@link HtmlElement} as lookup base.
     */
    @Test
    public void testFindInElement() throws Throwable
    {
        HPU.find().in(getHtmlElement());
    }

    /**
     * Set an {@link HtmlElement} as lookup base. In case of <code>null</code> an exception is expected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindInElementNull()
    {
        HPU.find().in((HtmlElement) null);
    }

    /*
     * By
     */

    /**
     * Test valid XPath expression (total path).
     * 
     * @throws Throwable
     */
    @Test
    public void testByXPathExpression() throws Throwable
    {
        final List<?> raw = HPU.find().in(getHtmlPage("")).byXPath("/html/body/foo").raw();
        Assert.assertTrue("List is expected to be empty.", raw.isEmpty());
    }

    /**
     * Test valid XPath expression (function).
     * 
     * @throws Throwable
     */
    @Test
    public void testByXPathExpressionFunction() throws Throwable
    {
        final List<?> raw = HPU.find().in(getHtmlPage("")).byXPath("id('foo')").raw();
        Assert.assertTrue("List is expected to be empty.", raw.isEmpty());
    }

    /**
     * Test invalid XPath expression (exception expected).
     * 
     * @throws Throwable
     */
    @Test(expected = RuntimeException.class)
    public void testByXPathExpressionInvalid() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byXPath("#foo").raw();
    }

    /**
     * Test empty XPath expression (exception expected).
     * 
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByXPathExpressionEmpty() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byXPath("").raw();
    }

    /**
     * Test XPath expression set to <code>null</code> (exception expected).
     * 
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByXPathExpressionNull() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byXPath(null).raw();
    }

    /**
     * Test valid CSS locator.
     * 
     * @throws Throwable
     */
    @Test
    public void testByCssExpression() throws Throwable
    {
        final List<?> raw = HPU.find().in(getHtmlPage("")).byCss("#foo").raw();
        Assert.assertTrue("List is expected to be empty.", raw.isEmpty());
    }

    /**
     * Test invalid CSS locator (exception expected).
     * 
     * @throws Throwable
     */
    @Test(expected = RuntimeException.class)
    public void testByCssExpressionInvalid() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byCss("/html/body").raw();
    }

    /**
     * Test empty CSS locator (exception expected).
     * 
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByCssExpressionEmpty() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byCss("").raw();
    }

    /**
     * Test CSS locator set to <code>null</code> (exception expected).
     * 
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByCssExpressionNull() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byCss(null).raw();
    }

    /**
     * Test valid ID String.
     * 
     * @throws Throwable
     */
    @Test
    public void testByIdString() throws Throwable
    {
        // HPU.find().in(getHtmlPage("")).byId("foo").raw();
        try
        {
            HPU.findAsserted("bamm bam").in(getHtmlPage("")).byId("quark").raw();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    /**
     * Test empty ID String (exception expected).
     * 
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByIdStringEmpty() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byId("").raw();
    }

    /**
     * Test ID String set to <code>null</code> (exception expected).
     * 
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testByIdStringNull() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byId(null).raw();
    }

    /*
     * Lookup strategies
     */

    /**
     * Test XPath strategy finds the right element (exactly 1).
     * 
     * @throws Throwable
     */
    @Test
    public void testXPathOneResult() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                                         .in(getHtmlPageOne())
                                         .byXPath("//*[@name='foo']")
                                         .all();
        Assert.assertEquals("Exactly 1 result element expected.", 1, all.size());
    }

    /**
     * Test XPath strategy finds the right elements (exactly 2).
     * 
     * @throws Throwable
     */
    @Test
    public void testXPathManyResults() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                                         .in(getHtmlPageMany())
                                         .byXPath("//*[@name='foo']")
                                         .all();
        Assert.assertEquals("Exactly 2 result elements expected.", 2, all.size());
    }

    /**
     * Test CSS strategy finds the right element (exactly 1).
     * 
     * @throws Throwable
     */
    @Test
    public void testCssOneResult() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                                         .in(getHtmlPageOne())
                                         .byCss(".foo")
                                         .all();
        Assert.assertEquals("Exactly 1 result element expected.", 1, all.size());
    }

    /**
     * Test CSS strategy finds the right elements (exactly 2).
     * 
     * @throws Throwable
     */
    @Test
    public void testCssManyResults() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                                         .in(getHtmlPageMany())
                                         .byCss(".foo")
                                         .all();
        Assert.assertEquals("Exactly 2 result elements expected.", 2, all.size());
    }

    /**
     * Test ID strategy finds the right element (first out of many).
     * 
     * @throws Throwable
     */
    @Test
    public void testIdResult() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                                         .in(getHtmlPageMany())
                                         .byId("foo")
                                         .all();
        Assert.assertEquals("Exactly 1 result element expected.", 1, all.size());
        Assert.assertEquals("First out of 2 elements expected.", "1", all.get(0).getAttribute("nr"));
    }

    /*
     * 
     * Results access
     */

    /*
     * Raw
     */

    /**
     * Access unparsed results (many).
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsRawMany() throws Throwable
    {
        final List<?> raw = HPU.find()
                               .in(getHtmlPageMany())
                               .byXPath("//*[@class='foo']")
                               .raw();
        Assert.assertEquals("Exactly 2 result elements expected.", 2, raw.size());
    }

    /**
     * Access unparsed results (none).
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsRawNothing() throws Throwable
    {
        final List<?> raw = HPU.find().in(getHtmlPage("")).byXPath("//*[@class='foo']").raw();
        Assert.assertTrue("List is expected to be empty.", raw.isEmpty());
    }

    /*
     * All
     */

    /**
     * Access cast results (many).
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsAllMany() throws Throwable
    {
        final List<HtmlElement> all = HPU.find()
                                         .in(getHtmlPageMany())
                                         .byXPath("//*[@class='foo']")
                                         .all();
        Assert.assertEquals("Exactly 2 result elements expected.", 2, all.size());
    }

    /**
     * Access cast results (none).
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsAllNothing() throws Throwable
    {
        final List<HtmlElement> all = HPU.find().in(getHtmlPage("")).byXPath("//*[@class='foo']").all();
        Assert.assertTrue("List is expected to be empty.", all.isEmpty());
    }

    /*
     * Existence
     */

    /**
     * Test element existence check in case of valid results.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsExists() throws Throwable
    {
        final boolean exists = HPU.find()
                                  .in(getHtmlPageOne())
                                  .byXPath("//*[@class='foo']").exists();
        Assert.assertTrue("Existing element not found.", exists);
    }

    /**
     * Test element existence check in case of no results.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsExistsNot() throws Throwable
    {
        final boolean exists = HPU.find()
                                  .in(getHtmlPage(""))
                                  .byXPath("//*[@class='foo']").exists();
        Assert.assertFalse("Element not found falsely.", exists);
    }

    /*
     * First
     */

    /**
     * Get first element out of many.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsFirstMany() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPageMany())
                                       .byXPath("//*[@class='foo']").first();
        Assert.assertEquals("Existing element not found.", "1", element.getAttribute("nr"));
    }

    /**
     * Get first element out of one.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsFirstOne() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPageOne())
                                       .byXPath("//*[@class='foo']").first();
        Assert.assertEquals("Existing element not found.", "1", element.getAttribute("nr"));
    }

    /**
     * Get first element out of none (<code>null</code>).
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsFirstNone() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPage(""))
                                       .byXPath("//*[@class='foo']").first();
        Assert.assertNull("Existing element not found.", element);
    }

    /*
     * Last
     */

    /**
     * Get last element out of many.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsLastMany() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPageMany())
                                       .byXPath("//*[@class='foo']").last();
        Assert.assertEquals("Existing element not found.", "2", element.getAttribute("nr"));
    }

    /**
     * Get last element out of one.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsLastOne() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPageOne())
                                       .byXPath("//*[@class='foo']").last();
        Assert.assertEquals("Existing element not found.", "1", element.getAttribute("nr"));
    }

    /**
     * Get last element out of none (<code>null</code>).
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsLastNone() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPage(""))
                                       .byXPath("//*[@class='foo']").last();
        Assert.assertNull("Existing element not found.", element);
    }

    /*
     * Count
     */

    /**
     * Count all elements (2).
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsCountMany() throws Throwable
    {
        final int count = HPU.find()
                             .in(getHtmlPageMany())
                             .byXPath("//*[@class='foo']")
                             .count();
        Assert.assertEquals("Exactly 2 result elements expected.", 2, count);
    }

    /**
     * Count all elements (1).
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsCountOne() throws Throwable
    {
        final int count = HPU.find()
                             .in(getHtmlPageOne())
                             .byXPath("//*[@class='foo']").count();
        Assert.assertEquals("Exactly 1 result element expected.", 1, count);
    }

    /**
     * Count all elements (0).
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsCountNothing() throws Throwable
    {
        final int count = HPU.find().in(getHtmlPage("")).byXPath("//*[@class='foo']").count();
        Assert.assertEquals("Exactly 0 result element expected.", 0, count);
    }

    /*
     * Is count
     */

    /**
     * If expected count is negative an exception should be thrown.
     * 
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResultsIsCountNegative() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byXPath("//*[@class='foo']")
           .isCount(-1);
    }

    /**
     * Number of results matches expected number.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountOne() throws Throwable
    {
        final boolean isCount = HPU.find()
                                   .in(getHtmlPageOne())
                                   .byXPath("//*[@class='foo']").isCount(1);
        Assert.assertTrue("Asserted count should be true.", isCount);
    }

    /**
     * Number of results does not match expected number.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountOneFalse() throws Throwable
    {
        final boolean isCount = HPU.find()
                                   .in(getHtmlPageMany())
                                   .byXPath("//*[@class='foo']").isCount(1);
        Assert.assertFalse("Assertted count should be false.", isCount);
    }

    /**
     * Number of results matches expected number when we have many results.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountMany() throws Throwable
    {
        final boolean isCount = HPU.find().in(getHtmlPageMany()).byXPath("//*[@class='foo']").isCount(2);
        Assert.assertTrue("Asserted count should be true.", isCount);
    }

    /**
     * Number of results matches expected number when we have no results.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountNone() throws Throwable
    {
        final boolean isCount = HPU.find().in(getHtmlPage("")).byXPath("//*[@class='foo']").isCount(0);
        Assert.assertTrue("Asserted count should be true.", isCount);
    }

    /*
     * Is count (min/max)
     */

    /**
     * One result, both range limits are equal, number of results is in given range.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountRangeOne() throws Throwable
    {
        final boolean isCount = HPU.find()
                                   .in(getHtmlPageOne())
                                   .byXPath("//*[@class='foo']")
                                   .isCount(1, 1);
        Assert.assertTrue(isCount);
    }

    /**
     * Many results, number of results is in given range.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountRangeOneOrMany() throws Throwable
    {
        final boolean isCount = HPU.find()
                                   .in(getHtmlPageMany())
                                   .byXPath("//*[@class='foo']")
                                   .isCount(1, 2);
        Assert.assertTrue(isCount);
    }

    /**
     * Many results, lower limit is <code>0</code>, number of results is in given range.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountRangeNoneOrMany() throws Throwable
    {
        final boolean isCount = HPU.find()
                                   .in(getHtmlPageMany())
                                   .byXPath("//*[@class='foo']")
                                   .isCount(0, 2);
        Assert.assertTrue(isCount);
    }

    /**
     * No result, both range limits are <code>0</code>
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountRangeNone() throws Throwable
    {
        final boolean isCount = HPU.find()
                                   .in(getHtmlPage(""))
                                   .byXPath("//*[@class='foo']")
                                   .isCount(0, 0);
        Assert.assertTrue(isCount);
    }

    /**
     * One result, lower range limit is higher than number of results (must be <code>false</code>).
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIsCountRangeOutOfBounds() throws Throwable
    {
        final boolean isCount = HPU.find()
                                   .in(getHtmlPageOne())
                                   .byXPath("//*[@class='foo']").isCount(2, 3);
        Assert.assertFalse("Asserted count should be true.", isCount);
    }

    /**
     * Lower range limit is negative (exception expected).
     * 
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResultsIsCountNegativeMin() throws Throwable
    {
        HPU.find().in(getHtmlPage("")).byXPath("//*[@class='foo']").isCount(-1, 1);
    }

    /**
     * Upper range limit is lower than the lower range limit (exception expected).
     * 
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResultsIsCountMaxTooLow() throws Throwable
    {
        final boolean isCount = HPU.find().in(getHtmlPageMany()).byXPath("//*[@class='foo']").isCount(2, 1);
        Assert.assertTrue("Asserted count should be true.", isCount);
    }

    /*
     * Index
     */

    /**
     * Get first result.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIndexManyFirst() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPageMany())
                                       .byXPath("//*[@class='foo']").index(0);
        Assert.assertEquals("Expected 1st element.", "1", element.getAttribute("nr"));
    }

    /**
     * Get second result.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIndexManySecond() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPageMany())
                                       .byXPath("//*[@class='foo']").index(1);
        Assert.assertEquals("Expected 2nd element.", "2", element.getAttribute("nr"));
    }

    /**
     * Query first result but there are no results at all.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsIndexNoneFirst() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPage(""))
                                       .byXPath("//*[@class='foo']").index(0);
        Assert.assertNull("No such index.", element);
    }

    /**
     * Negative index number.
     * 
     * @throws Throwable
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResultsIndexNegative() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byXPath("//*[@class='foo']").index(-1);
    }

    /*
     * Random
     */

    /**
     * Test random result element selection.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsRandom() throws Throwable
    {
        final Results results = HPU.find()
                                   .in(getHtmlPageMany())
                                   .byXPath("//*[@class='foo']");
        int countNr1 = 0;
        int countNr2 = 0;

        for (int i = 0; i < 100; i++)
        {
            final HtmlElement element = results.random();
            final String nr = element.getAttribute("nr");
            if (nr.equals("1"))
            {
                countNr1++;
            }
            else if (nr.equals("2"))
            {
                countNr2++;
            }
            else
            {
                Assert.fail("Unexpected element found.");
            }
        }
        Assert.assertTrue("Element 1 was never selected.", countNr1 > 0);
        Assert.assertTrue("Element 2 was never selected.", countNr2 > 0);
    }

    /**
     * Select random element out of <code>0</code> elements.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsRandomNone() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPage(""))
                                       .byXPath("//*[@class='foo']").random();
        Assert.assertNull("Found element falsely.", element);
    }

    /*
     * Single
     */

    /**
     * Get one and only result element.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsSingle() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPageOne())
                                       .byXPath("//*[@class='foo']").random();
        Assert.assertEquals("Unexpected element found.", "1", element.getAttribute("nr"));
    }

    /**
     * Many results, so there's no 'single' result.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsSingleMany() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPageMany())
                                       .byXPath("//*[@class='foo']").single();
        Assert.assertNull("Found element falsely.", element);
    }

    /**
     * No result.
     * 
     * @throws Throwable
     */
    @Test
    public void testResultsSingleNone() throws Throwable
    {
        final HtmlElement element = HPU.find()
                                       .in(getHtmlPage(""))
                                       .byXPath("//*[@class='foo']").single();
        Assert.assertNull("Found element falsely.", element);
    }

    /*
     * Asserted Lookup
     */

    /**
     * Raw: Assertion fulfilled (at least 1 result), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedRaw() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().raw();
    }

    /**
     * Raw: Assertion failed (no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedRawFail() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().raw();
    }

    /**
     * All: Assertion fulfilled (at least 1 result), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedAll() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().all();
    }

    /**
     * All: Assertion failed (no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedAllFail() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().all();
    }

    /**
     * Count: Assertion fulfilled (at least 1 result), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedCount() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().count();
    }

    /**
     * Count: Assertion failed (no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedCountFail() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().count();
    }

    /**
     * Exists: Assertion fulfilled (at least 1 result), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedExists() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().exists();
    }

    /**
     * Exists: Assertion failed (no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedExistsFail() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().exists();
    }

    /**
     * IsCount: Assertion fulfilled (at least 1 result), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedIsCount() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().isCount(1);
    }

    /**
     * IsCount: Assertion failed (count doesn't match)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIsCountFail() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().isCount(2);
    }

    /**
     * IsCount: Assertion failed (count matches but no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIsCountFail2() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().isCount(0);
    }

    /**
     * IsCount: Assertion fulfilled (at least 1 result, number of results within given range), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedIsCountRange() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().isCount(0, 1);
    }

    /**
     * IsCount: Assertion failed (number of results within given range, but no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIsCountRangeFail() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().isCount(0, 1);
    }

    /**
     * IsCount: Assertion failed (at least 1 result, but number of results not within given range)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIsCountRangeFail2() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().isCount(2, 3);
    }

    /**
     * Index: Assertion fulfilled (at least 1 result), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedIndex() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().index(0);
    }

    /**
     * Index: Assertion failed (no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIndexFail() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().index(0);
    }

    /**
     * Index: Assertion failed (many results but index out of bounds)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedIndexFail2() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().index(5);
    }

    /**
     * First: Assertion fulfilled (at least 1 result), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedFirst() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().first();
    }

    /**
     * First: Assertion failed (no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedFirstFail() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().first();
    }

    /**
     * Last: Assertion fulfilled (at least 1 result), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedLast() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().last();
    }

    /**
     * Last: Assertion failed (no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedLastFail() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().last();
    }

    /**
     * Random: Assertion fulfilled (at least 1 result), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedRandom() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().random();
    }

    /**
     * Random: Assertion failed (no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedRandomFail() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().random();
    }

    /**
     * Single: Assertion fulfilled (exactly 1 result), no error
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedSingle() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageOne())
           .byId("foo").asserted().single();
    }

    /**
     * Single: Assertion failed (too many results)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedSingleFailMany() throws Throwable
    {
        HPU.find()
           .in(getHtmlPageMany())
           .byXPath("//*[@class='foo']").asserted().single();
    }

    /**
     * Single: Assertion failed (no result)
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedSingleFailNone() throws Throwable
    {
        HPU.find()
           .in(getHtmlPage(""))
           .byId("foo").asserted().single();
    }

    /**
     * Test custom assertion message.
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedCustomMessage() throws Throwable
    {
        try
        {
            HPU.find().in(getHtmlPage("")).byId("foo").asserted("myCustomMessage").raw();
            throw new TestException();
        }
        catch (final AssertionError e)
        {
            Assert.assertEquals("myCustomMessage", e.getMessage());
        }
    }

    /**
     * Test assertion shortcut (assertion fulfilled).
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedShortcut() throws Throwable
    {
        HPU.findAsserted().in(getHtmlPageOne()).byId("foo").raw();
    }

    /**
     * Test assertion shortcut (assertion failed).
     * 
     * @throws Throwable
     */
    @Test(expected = AssertionError.class)
    public void testAssertedShortcutFail() throws Throwable
    {
        HPU.findAsserted().in(getHtmlPage("")).byId("foo").raw();
    }

    /**
     * Test assertion shortcut with custom message.
     * 
     * @throws Throwable
     */
    @Test
    public void testAssertedShortcutFailCustomMessage() throws Throwable
    {
        try
        {
            HPU.findAsserted("myCustomMessage").in(getHtmlPage("")).byId("foo").raw();
            throw new TestException();
        }
        catch (final AssertionError e)
        {
            Assert.assertEquals("myCustomMessage", e.getMessage());
        }
    }

    /*
     * Chaining
     */

    /**
     * Test lookup strategy chain.
     * 
     * @throws Throwable
     */
    @Test
    public void testStrategyChaining() throws Throwable
    {
        final HtmlElement element = HPU.findAsserted()
                                       .in(getHtmlPage("<html><body><div id=\"a\"><div class=\"aa\"><div class=\"aaa\" nr=\"1\"></div></div></div><div id=\"b\"><div class=\"aa\"><div class=\"aaa\" nr=\"2\"></div></div></div></body><html>"))
                                       .byId("a").byCss(".aa").byXPath("./*[@class='aaa']").single();
        Assert.assertEquals("Existing element not found.", "1", element.getAttribute("nr"));
    }

    @Test
    public void testStrategyChainingSeparated() throws Throwable
    {
        // lookup by ID
        Results results = HPU.findAsserted()
                             .in(getHtmlPage("<html><body><div id=\"a\"><div class=\"aa\"><div class=\"aaa\" nr=\"1\"></div></div></div><div id=\"b\"><div class=\"aa\"><div class=\"aaa\" nr=\"2\"></div></div></div></body><html>"))
                             .byId("a");

        // refine by CSS
        results = results.byCss(".aa");

        // refine by XPath
        results = results.byXPath("./*[@class='aaa']");
        final HtmlElement element = results.single();
        Assert.assertEquals("Existing element not found.", "1", element.getAttribute("nr"));
    }

    /**
     * Creates an {@link HtmlPage} object from the passed HTML source code.
     * 
     * @throws IOException
     * @throws MalformedURLException
     * @throws FailingHttpStatusCodeException
     */
    public HtmlPage getHtmlPage(final String htmlSource) throws FailingHttpStatusCodeException, MalformedURLException, IOException
    {
        final WebClient webClient = new WebClient();

        final MockWebConnection connection = new MockWebConnection();
        connection.setDefaultResponse(htmlSource);
        webClient.setWebConnection(connection);

        return webClient.getPage("http://localhost/");
    }

    /**
     * Page containing 2 <code>div</code> elements but only 1 with name/class/id <code>'foo'</code>.
     * 
     * @return
     * @throws FailingHttpStatusCodeException
     * @throws MalformedURLException
     * @throws IOException
     */
    public HtmlPage getHtmlPageOne() throws FailingHttpStatusCodeException, MalformedURLException, IOException
    {
        return getHtmlPage("<html><body><div  id=\"foo\" name=\"foo\" class=\"foo\" nr=\"1\"></div><div id=\"bar\" name=\"bar\" class=\"bar\" nr=\"2\"></div></body></html>");
    }

    /**
     * Page containing 3 <code>div</code> elements but only 2 with name/class/id <code>'foo'</code>.
     * 
     * @return
     * @throws FailingHttpStatusCodeException
     * @throws MalformedURLException
     * @throws IOException
     */
    public HtmlPage getHtmlPageMany() throws FailingHttpStatusCodeException, MalformedURLException, IOException
    {
        return getHtmlPage("<html><body><div id=\"foo\" name=\"foo\" class=\"foo\" nr=\"1\"></div><div id=\"foo\" name=\"foo\" class=\"foo\" nr=\"2\"></div><div nr=\"3\"></body></html>");
    }

    /**
     * Creates an {@link HtmlInput}.
     * 
     * @throws IOException
     * @throws MalformedURLException
     * @throws FailingHttpStatusCodeException
     */
    public HtmlElement getHtmlElement() throws FailingHttpStatusCodeException, MalformedURLException, IOException
    {
        final HtmlPage page = getHtmlPage("");

        final AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(null, "name", "name", "", "myName");
        atts.addAttribute(null, "value", "value", "", "myValue");
        atts.addAttribute(null, "type", "type", "", "text");
        final HtmlInput input = (HtmlInput) InputElementFactory.instance.createElement(page, HtmlInput.TAG_NAME, atts);

        return input;
    }

    /**
     * Unchecked exception for testing purposes.
     */
    private static class TestException extends RuntimeException
    {
        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1L;
    }
}
