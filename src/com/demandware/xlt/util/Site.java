package com.demandware.xlt.util;

/**
 * Use a single enum value for each site to test to easily tell one site from the other during the test. Giving an enum
 * constructor parameter <code>null</code> causes the test and actions neither to not have marked names nor will the
 * site specific property feature work. So with <code>null</code> you have just one default site context and stick to
 * the used naming patterns. It's recommended that the site ID given as constructor parameter is unique.
 */
public enum Site
{
    DEFAULT(null),
    // MY_EXAMPLE("MyExampleSite")
    // MY_OTHER_SITE("MyOtherSite")
    // MY_THIRD_SITE("MTS")
    ;

    /** Suffix added to test/action/request names */
    private final String suffix;

    /**
     * Prefix used to identify site specific properties.<br>
     * So if the default property is <code>foo=bar</code> the site specific property might be <code>x.foo=bar</code>
     * (while <code>x</code> is the site ID).
     */
    private final String propertyPrefix;

    /**
     * Create a Site object with given ID. Please note that the ID will be used to generate site specific suffix for
     * test, action, and request names as well as a property prefix. The suffix will be upper case while the prefix will
     * be lower case. Examples:<br>
     * <table border=1 style="text-align:center">
     * <tr>
     * <th>ID</th>
     * <th>Property Prefix</th>
     * <th>Name Suffix</th>
     * </tr>
     * <tr>
     * <td>foo</td>
     * <td>foo.</td>
     * <td>_FOO</td>
     * </tr>
     * <tr>
     * <td>Foo</td>
     * <td>foo.</td>
     * <td>_FOO</td>
     * </tr>
     * <tr>
     * <td>FOO</td>
     * <td>foo.</td>
     * <td>_FOO</td>
     * </tr>
     * <tr>
     * <td>&lt;null&gt;</td>
     * <td>&lt;empty&gt;</td>
     * <td>&lt;empty&gt;</td>
     * </tr>
     * </table>
     * 
     * @param id
     *            Unique site specific ID. It must not contain any whitespaces or a dot. This ID is used to tell one
     *            site from the other. Value <code>null</code> is permitted and will mark the default site. In that case
     *            no suffix or prefix will be generated.
     */
    private Site(final String id)
    {
        this.suffix = id == null ? "" : "_" + id.toUpperCase();
        this.propertyPrefix = id == null ? "" : id.toLowerCase() + ".";
    }

    /**
     * Get the site specific suffix. The suffix is built from the site ID by turning it to upper case and add a leading
     * under score. It will never be <code>null</code> but might be empty if the {@link Site} ID was set to
     * <code>null</code>. The suffix is used to mark test case, action, and request names.<br>
     * Examples:
     * <table border=1 style="text-align:center">
     * <tr>
     * <th>ID</th>
     * <th>Name Suffix</th>
     * </tr>
     * <tr>
     * <td>foo</td>
     * <td>_FOO</td>
     * </tr>
     * <tr>
     * <td>Foo</td>
     * <td>_FOO</td>
     * </tr>
     * <tr>
     * <td>FOO</td>
     * <td>_FOO</td>
     * </tr>
     * <tr>
     * <td>&lt;null&gt;</td>
     * <td>&lt;empty&gt;</td>
     * </tr>
     * </table>
     * 
     * @return site specific suffix
     */
    public String getSuffix()
    {
        return suffix;
    }

    /**
     * Get the site specific property prefix. This prefix is built from the {@link Site} ID by turning it to lower case
     * and append a trailing dot. It will never be <code>null</code> but might be empty if the {@link Site} ID was set
     * to <code>null</code>. The prefix is used to mark properties for use with the current site.<br>
     * Examples:
     * <table border=1 style="text-align:center">
     * <tr>
     * <th>ID</th>
     * <th>Property Prefix</th>
     * </tr>
     * <tr>
     * <td>foo</td>
     * <td>foo.</td>
     * </tr>
     * <tr>
     * <td>Foo</td>
     * <td>foo.</td>
     * </tr>
     * <tr>
     * <td>FOO</td>
     * <td>foo.</td>
     * </tr>
     * <tr>
     * <td>&lt;null&gt;</td>
     * <td>&lt;empty&gt;</td>
     * </tr>
     * </table>
     * 
     * @return site specific property prefix
     */
    public String getPropertyPrefix()
    {
        return propertyPrefix;
    }
}
