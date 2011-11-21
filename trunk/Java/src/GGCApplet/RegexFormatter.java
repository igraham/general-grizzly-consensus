package GGCApplet;

import java.text.ParseException;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

import java.util.regex.PatternSyntaxException;



import javax.swing.text.DefaultFormatter;


/**
 * This is actually an example given by the java sun group, but it was not included in the regular java libraries.
 * @author Oracle's Java Sun group
 *
 */
@SuppressWarnings("serial")
public class RegexFormatter extends DefaultFormatter {

    private Pattern pattern;

    private Matcher matcher;



    public RegexFormatter() {

        super();

    }



    public RegexFormatter(String pattern) throws PatternSyntaxException {

        this();

        setPattern(Pattern.compile(pattern));

    }



    public RegexFormatter(Pattern pattern) {

        this();

        setPattern(pattern);

    }



    public void setPattern(Pattern pattern) {

        this.pattern = pattern;

    }



    public Pattern getPattern() {

        return pattern;

    }



    protected void setMatcher(Matcher matcher) {

        this.matcher = matcher;

    }



    protected Matcher getMatcher() {

        return matcher;

    }



    public Object stringToValue(String text) throws ParseException {

        Pattern pattern = getPattern();



        if (pattern != null) {

            Matcher matcher = pattern.matcher(text);



            if (matcher.matches()) {

                setMatcher(matcher);

                return super.stringToValue(text);

            }

            throw new ParseException("Pattern did not match", 0);

        }

        return text;

    }

}

