package chapter14.solution;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chapter14.solution.ArgsException.ErrorCode;
import org.junit.jupiter.api.Test;

public class ArgsTest {

  @Test
  public void testCreateWithNoSchemaOrArguments() throws Exception {
    Args args = new Args("", new String[0]);
    assertEquals(0, args.cardinality());
  }

  @Test
  public void testWithNoSchemaButWithOneArgument() throws Exception {

    assertThatThrownBy(() -> new Args("", new String[]{"-x"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.UNEXPECTED_ARGUMENT);

    assertThatThrownBy(() -> new Args("", new String[]{"-x"}))
        .extracting("errorArgumentId")
        .isEqualTo('x');
  }

  @Test
  public void testWithSchemaButWithNoArguments() throws Exception {
    assertThatThrownBy(() -> new Args("x", new String[0]))
        .isInstanceOf(ArgsException.class)
        .hasMessage("'[]' is empty");
  }

  @Test
  public void testWithNoSchemaButWithMultipleArguments() throws Exception {
    assertThatThrownBy(() -> new Args("", new String[]{"-x", "-y"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.UNEXPECTED_ARGUMENT);

    assertThatThrownBy(() -> new Args("", new String[]{"-x", "-y"}))
        .extracting("errorArgumentId")
        .isEqualTo('x');
  }

  @Test
  public void testNonLetterSchema() throws Exception {
    assertThatThrownBy(() -> new Args("*", new String[]{}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.INVALID_ARGUMENT_NAME);

    assertThatThrownBy(() -> new Args("*", new String[]{}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorArgumentId")
        .isEqualTo('*');
  }

  @Test
  public void testInvalidArgumentFormat() throws Exception {
    assertThatThrownBy(() -> new Args("f~", new String[]{}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.INVALID_ARGUMENT_FORMAT);

    assertThatThrownBy(() -> new Args("f~", new String[]{}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorArgumentId")
        .isEqualTo('f');
  }

  @Test
  public void testSimpleBooleanTruePresent() throws Exception {
    Args args = new Args("x", new String[]{"-x", "true"});
    assertEquals(1, args.cardinality());
    assertEquals(true, args.getBoolean('x'));
  }

  @Test
  public void testSimpleBooleanFalsePresent() throws Exception {
    Args args = new Args("x", new String[]{"-x", "false"});
    assertEquals(1, args.cardinality());
    assertEquals(false, args.getBoolean('x'));
  }

  @Test
  public void testMissingBooleanArgument() throws Exception {
    assertThatThrownBy(() -> new Args("x", new String[]{"-x"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.MISSING_BOOLEAN);

    assertThatThrownBy(() -> new Args("x", new String[]{"-x"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorArgumentId")
        .isEqualTo('x');
  }

  @Test
  public void testInvalidBoolean() throws Exception {
    Args args = new Args("x", new String[]{"-x", "Truthy"});
    assertEquals(1, args.cardinality());
    assertEquals(false, args.getBoolean('x'));
  }

  @Test
  public void testSpacesInFormat() throws Exception {
    Args args = new Args("x, y", new String[]{"-xy", "true", "false"});
    assertEquals(2, args.cardinality());
    assertTrue(args.has('x'));
    assertTrue(args.has('y'));
    assertEquals(true, args.getBoolean('x'));
    assertEquals(false, args.getBoolean('y'));
  }


  @Test
  public void testInvalidArgumentValueFormat() throws Exception {
    assertThatThrownBy(() -> new Args("x, y", new String[]{"xy", "true", "false"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.INVALID_ARGUMENT_FORMAT);

    assertThatThrownBy(() -> new Args("x, y", new String[]{"xy", "true", "false"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorArgumentId")
        .isEqualTo('-');
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
    assertThatThrownBy(() -> new Args("x*", new String[]{"-x"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.MISSING_STRING);

    assertThatThrownBy(() -> new Args("x*", new String[]{"-x"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorArgumentId")
        .isEqualTo('x');
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
    assertThatThrownBy(() -> new Args("x#", new String[]{"-x", "Forty two"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.INVALID_INTEGER);

    assertThatThrownBy(() -> new Args("x#", new String[]{"-x", "Forty two"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorArgumentId")
        .isEqualTo('x');

    assertThatThrownBy(() -> new Args("x#", new String[]{"-x", "Forty two"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorParameter")
        .isEqualTo("Forty two");
  }

  @Test
  public void testMissingInteger() throws Exception {
    assertThatThrownBy(() -> new Args("x#", new String[]{"-x"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.MISSING_INTEGER);

    assertThatThrownBy(() -> new Args("x#", new String[]{"-x"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorArgumentId")
        .isEqualTo('x');
  }

  @Test
  public void testSimpleDoublePresent() throws Exception {
    Args args = new Args("x##", new String[]{"-x", "42.3"});
    assertEquals(1, args.cardinality());
    assertTrue(args.has('x'));
    assertEquals(42.3, args.getDouble('x'), .001);
  }

  @Test
  public void testInvalidDouble() throws Exception {
    assertThatThrownBy(() -> new Args("x##", new String[]{"-x", "Forty two"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.INVALID_DOUBLE);

    assertThatThrownBy(() -> new Args("x##", new String[]{"-x", "Forty two"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorArgumentId")
        .isEqualTo('x');

    assertThatThrownBy(() -> new Args("x##", new String[]{"-x", "Forty two"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorParameter")
        .isEqualTo("Forty two");
  }

  @Test
  public void testMissingDouble() throws Exception {
    assertThatThrownBy(() -> new Args("x##", new String[]{"-x"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorCode")
        .isEqualTo(ErrorCode.MISSING_DOUBLE);

    assertThatThrownBy(() -> new Args("x##", new String[]{"-x"}))
        .isInstanceOf(ArgsException.class)
        .extracting("errorArgumentId")
        .isEqualTo('x');
  }
}
