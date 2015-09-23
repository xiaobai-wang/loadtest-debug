package com.demandware.xlt.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.xceptance.common.util.ParameterCheckUtils;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.BasicPageUtils;
import com.xceptance.xlt.api.util.LightweightHtmlPageUtils;
import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.api.util.elementLookup.Results;

/**
 * The {@link FormUtils} class provides some useful helper methods to make dealing with {@link HtmlPage} objects easier.
 * When using the plain HtmlUnit API, similar pieces of code have to be written again and again. Using this class, test
 * case actions are often shorter and easier to understand.
 * 
 * @see AbstractHtmlPageAction
 * @see LightweightHtmlPageUtils
 */
public class FormUtils extends BasicPageUtils
{
    /**
     * <code>false</code> if test is executed in load test mode. Outside a load test (usually debug mode) test suite
     * performance has not the top priority but more logging will help to identify issues.
     */
    private static final boolean isNotPerformanceMode = !Context.isLoadTest();

    /**
     * Finds the HTML select element with the given locator in the specified form and selects the option with the passed
     * value.
     * 
     * @param selectLocator
     *            the locator of the select element
     * @param optionValue
     *            the value of the option element to select
     */
    public static void select(final Results selectLocator, final String optionValue)
    {
        if (isNotPerformanceMode)
        {
            isNotNull(selectLocator);
            ParameterCheckUtils.isNotNull(optionValue, "optionValue");
        }

        // Get the select element.
        final HtmlSelect select = getAsserted(selectLocator);

        // Check if given option value fits to any of the select's option.
        try
        {
            select.getOptionByValue(optionValue);
        }
        catch (final ElementNotFoundException e)
        {
            Assert.fail("Value '" + optionValue + "' not present in '" + selectLocator.getLocatorDescription() + "'");
        }

        // Select the given option value
        select.setSelectedAttribute(optionValue, true);

        if (isNotPerformanceMode)
        {
            XltLogger.runTimeLogger.info(String.format("Selecting option: %s.%s", getIdOrName(select),
                                                       optionValue));
        }
    }

    /**
     * Finds the HTML select element with the given ID in the specified form and selects the option with the passed
     * value.
     * 
     * @param selectId
     *            the ID of the select element
     * @param optionValue
     *            the value of the option element to select
     */
    public static void selectByID(final String selectId, final String optionValue)
    {
        // Convert ID to locator and proceed with selection.
        select(getByID(selectId), optionValue);
    }

    /**
     * Finds the HTML select element with the given locator in the specified form and set one of the options selected
     * randomly. Disabled option will be ignored. This method allows you to exclude first and last option.
     * 
     * @param selectLocator
     *            the locator of the select element
     * @param excludeFirst
     *            whether to exclude the first option element
     * @param excludeLast
     *            whether to exclude the last option element
     */
    public static void selectRandomly(final Results selectLocator, final boolean excludeFirst, final boolean excludeLast)
    {
        if (isNotPerformanceMode)
        {
            isNotNull(selectLocator);
        }

        // Get the select element.
        final HtmlSelect select = getAsserted(selectLocator);

        // Select by random.
        selectRandomly(select, excludeFirst, excludeLast);
    }

    /**
     * Finds the HTML select element with the given ID in the specified form and selects one of the options randomly.
     * 
     * @param selectId
     *            the ID of the select element
     */
    public static void selectRandomlyByID(final String selectId)
    {
        // Select while not excluding any option.
        selectRandomlyByID(selectId, false, false);
    }

    /**
     * Finds the HTML select element with the given ID in the specified form and selects one of the options randomly.
     * This method allows you to exclude the first option.
     * 
     * @param selectId
     *            the ID of the select element
     * @param excludeFirst
     *            whether to exclude the first option element
     */
    public static void selectRandomlyByID(final String selectId, final boolean excludeFirst)
    {
        // Select while not excluding any option.
        selectRandomlyByID(selectId, excludeFirst, false);
    }

    /**
     * Finds the HTML select element with the given ID in the specified form and set one of the options selected
     * randomly. Disabled option will be ignored. This method allows you to exclude first and last option.
     * 
     * @param selectId
     *            the ID of the select element
     * @param excludeFirst
     *            whether to exclude the first option element
     * @param excludeLast
     *            whether to exclude the last option element
     */
    public static void selectRandomlyByID(final String selectId, final boolean excludeFirst, final boolean excludeLast)
    {
        selectRandomly(getByID(selectId), excludeFirst, excludeLast);
    }

    /**
     * Selects one of the options of the given select randomly. Disabled options will be ignored. This method allows you
     * to exclude first and last option.
     * 
     * @param select
     *            the select element
     * @param excludeFirst
     *            whether to exclude the first option element
     * @param excludeLast
     *            whether to exclude the last option element
     */
    public static void selectRandomly(final HtmlSelect select, final boolean excludeFirst, final boolean excludeLast)
    {
        if (isNotPerformanceMode)
        {
            ParameterCheckUtils.isNotNull(select, "select");
        }

        // Get the option elements.
        final List<HtmlOption> origOptions = new ArrayList<HtmlOption>(select.getOptions());

        // Extract all non-disabled options.
        final List<HtmlOption> options = new ArrayList<HtmlOption>(origOptions.size());
        for (int i = 0; i < origOptions.size(); i++)
        {
            final HtmlOption option = origOptions.get(i);
            if (!option.isDisabled())
            {
                options.add(option);
            }
        }

        // Select one of the non-disabled options.
        final HtmlOption option = pickOneRandomly(options, excludeFirst, excludeLast);
        select.setSelectedAttribute(option, true);

        if (isNotPerformanceMode)
        {
            XltLogger.runTimeLogger.info(String.format("Setting select value: %s = %s", getIdOrName(select),
                                                       option.getValueAttribute()));
        }
    }

    /**
     * Returns the value of the "id" attribute or, if there is no such attribute, the "name" attribute of the given HTML
     * element.
     * 
     * @param element
     *            the HTML element in question
     * @return the ID or the name or <code>&lt;unnamed&gt;</code> if neither an ID nor a name is available
     */
    private static String getIdOrName(final HtmlElement element)
    {
        // parameter check
        ParameterCheckUtils.isNotNull(element, "element");

        // Get the ID.
        String result = element.getId();

        // If no ID is available get the name.
        if (StringUtils.isEmpty(result))
        {
            result = element.getAttribute("name");
        }

        // If even no name is available fall back to a generic value.
        if (StringUtils.isEmpty(result))
        {
            result = "<unnamed>";
        }

        return result;
    }

    /**
     * Finds the HTML check box input element with the given locator in the specified form and sets its value.
     * 
     * @param checkboxLocator
     *            the locator of the check box
     * @param isChecked
     *            the new check box value
     */
    public static void checkCheckbox(final Results checkboxLocator, final boolean isChecked)
    {
        if (isNotPerformanceMode)
        {
            isNotNull(checkboxLocator);
            ParameterCheckUtils.isNotNull(isChecked, "isChecked");
        }

        // Get the check box and set the selection state.
        final HtmlCheckBoxInput checkbox = getAsserted(checkboxLocator);
        checkbox.setChecked(isChecked);

        if (isNotPerformanceMode)
        {
            XltLogger.runTimeLogger.info(String.format("Setting checkbox value: %s = %b", getIdOrName(checkbox), isChecked));
        }
    }

    /**
     * Finds the HTML check box input element with the given ID in the specified form and sets its value.
     * 
     * @param checkboxId
     *            the ID of the check box
     * @param isChecked
     *            the new check box value
     */
    public static void checkCheckboxByID(final String checkboxId, final boolean isChecked)
    {
        checkCheckbox(getByID(checkboxId), isChecked);
    }

    /**
     * Finds the HTML input element with the given locator in the specified form and sets its value.
     * 
     * @param inputLocator
     *            the locator of the input element
     * @param value
     *            the new value
     */
    public static void setInputValue(final Results inputLocator, final String value)
    {
        if (isNotPerformanceMode)
        {
            isNotNull(inputLocator);
        }

        // Get the input element and set the new value.
        final HtmlInput input = getAsserted(inputLocator);
        input.setValueAttribute(value);

        if (isNotPerformanceMode)
        {
            XltLogger.runTimeLogger.info(String.format("Setting input value: %s = %s", getIdOrName(input), value));
        }
    }

    /**
     * Finds the HTML input element with the given ID in the specified form and sets its value.
     * 
     * @param inputId
     *            the ID of the input element
     * @param value
     *            the new value
     */
    public static void setInputValueByID(final String inputId, final String value)
    {
        setInputValue(getByID(inputId), value);
    }

    /**
     * Finds the HTML radio button element with the given locator in the specified form and checks it.
     *
     * @param radioLocator
     *            the locator of the radio button
     */
    public static void checkRadioButton(final Results radioLocator)
    {
        if (isNotPerformanceMode)
        {
            isNotNull(radioLocator);
        }

        // Get the requested radio button and set the given selection state.
        final HtmlRadioButtonInput radioButton = getAsserted(radioLocator);
        radioButton.setChecked(true);

        if (isNotPerformanceMode)
        {
            XltLogger.runTimeLogger.info(String.format("Checking radio button: %s = %s", getIdOrName(radioButton),
                                                       radioLocator.getLocatorDescription()));
        }
    }

    /**
     * Finds the HTML radio button element with the given ID in the specified form and checks it.
     * 
     * @param id
     *            the ID of the radio button
     */
    public static void checkRadioButtonByID(final String id)
    {
        checkRadioButton(getByID(id));
    }

    /**
     * Get locator object for given ID
     * 
     * @param id
     *            the ID to lookup
     * @return the built locator
     */
    private static Results getByID(final String id)
    {
        return Page.findAsserted().byId(id);
    }

    /**
     * Get the element selected by the given locator. The element is asserted to exist and to be unique.
     * 
     * @param locator
     *            describes the element to lookup
     * @return the selected element
     */
    private static <T extends HtmlElement> T getAsserted(final Results locator)
    {
        return locator.asserted().single();
        // return locator.asserted();
    }

    /**
     * Check that the given locator is not <code>null</code>.
     * 
     * @param locator
     *            the locator to check
     * @throws IllegalArgumentException
     *             if the passed argument is <code>null</code>
     */
    private static void isNotNull(final Results locator) throws IllegalArgumentException
    {
        if (locator == null)
        {
            throw new IllegalArgumentException("Locator must not be NULL");
        }
    }
}
