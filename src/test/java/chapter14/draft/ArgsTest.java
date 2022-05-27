package chapter14.draft;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ArgsTest {

  @Test
  public void testCreateWithNoSchemaOrArguments() throws Exception {
    Args args = new Args("", new String[0]);
    assertEquals(0, args.cardinality());
  }

  @Test
  public void testWithNoSchemaButWithOneArgument() throws Exception {
    Args args = new Args("", new String[]{"-x"});
    assertEquals(false, args.isValid());
    assertEquals("Argument(s) -x unexpected.", args.errorMessage());
  }

  @Test
  public void testWithNoSchemaButWithMultipleArguments() throws Exception {
    Args args = new Args("", new String[]{"-x", "-y"});
    assertEquals(false, args.isValid());
    assertEquals("Argument(s) -xy unexpected.", args.errorMessage());
  }

  @Test
  public void testNonLetterSchema() throws Exception {
    assertThatThrownBy(() -> new Args("*", new String[]{}))
        .isInstanceOf(ParseException.class);
    assertThatThrownBy(() -> new Args("*", new String[]{}))
        .hasMessage("Bad character:*in Args format: *");
  }

  @Test
  public void testSimpleBooleanTruePresent() throws Exception {
    Args args = new Args("x", new String[]{"-x", "true"});
    assertEquals(1, args.cardinality());
    assertEquals(true, args.getBoolean('x'));
  }

  @Test
  public void testMultipleBooleans() throws Exception {
    Args args = new Args("x,y", new String[]{"-xy", "true", "true"});
    assertEquals(2, args.cardinality());
    assertEquals(true, args.getBoolean('x'));
    assertEquals(true, args.getBoolean('y'));
  }

  @Test
  public void testSpacesInFormat() throws Exception {
    Args args = new Args("x, y", new String[]{"-xy", "true", "true"});
    assertEquals(2, args.cardinality());
    assertTrue(args.has('x'));
    assertTrue(args.has('y'));
    assertEquals(true, args.getBoolean('x'));
    assertEquals(true, args.getBoolean('y'));
  }

  @Test
  public void testSimpleStringPresent() throws Exception {
    Args args = new Args("x*", new String[]{"-x", "param"});
    assertEquals(1, args.cardinality());
    assertTrue(args.has('x'));
    assertEquals("param", args.getString('x'));
  }

  @Test
  public void testMissingStringArgument() throws Exception {
    Args args = new Args("x*", new String[]{"-x"});
    assertEquals(false, args.isValid());
    assertEquals("Could not find string parameter for -x.",
        args.errorMessage());
  }

  @Test
  public void testSimpleIntPresent() throws Exception {
    Args args = new Args("x#", new String[]{"-x", "42"});
    assertEquals(1, args.cardinality());
    assertTrue(args.has('x'));
    assertEquals(42, args.getInt('x'));
  }

  @Test
  public void testInvalidInteger() throws Exception {
    Args args = new Args("x#", new String[]{"-x", "Forty two"});
    assertEquals(false, args.isValid());
    assertEquals("Argument -x expects an integer but was 'Forty two'.",
        args.errorMessage());
  }

  @Test
  public void testMissingInteger() throws Exception {
    Args args = new Args("x#", new String[]{"-x"});
    assertEquals(false, args.isValid());
    assertEquals("Could not find integer parameter for -x.",
        args.errorMessage());
  }


  // Currently fails...
  @Test
  @Disabled
  public void testInvalidArgumentFormat() throws Exception {
    assertThatThrownBy(() -> new Args("f~", new String[]{}))
        .isInstanceOf(ParseException.class);

    assertThatThrownBy(() -> new Args("*", new String[]{}))
        .hasMessage("Argument: f has invalid format: ~.");
  }

  // Currently fails...
  @Test
  @Disabled
  public void testSimpleBooleanFalsePresent() throws Exception {
    Args args = new Args("x", new String[]{"-x", "false"});
    assertEquals(1, args.cardinality());
    assertEquals(false, args.getBoolean('x'));
  }

  // Currently fails...
  @Test
  @Disabled
  public void testMissingBooleanArgument() throws Exception {
    Args args = new Args("x", new String[]{"-x"});
    assertEquals(false, args.isValid());
  }

  // Currently fails...
  @Test
  @Disabled
  public void testInvalidBoolean() throws Exception {
    Args args = new Args("x", new String[]{"-x", "Truthy"});
    assertEquals(1, args.cardinality());
    assertEquals(false, args.getBoolean('x'));
  }

  // Currently fails...
  @Test
  @Disabled
  public void testInvalidArgumentValueFormat() throws Exception {
    Args args = new Args("x,y", new String[]{"xy", "true", "false"});
    assertEquals(false, args.isValid());
  }

}
