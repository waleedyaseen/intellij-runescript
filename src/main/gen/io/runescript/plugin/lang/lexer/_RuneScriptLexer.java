// Generated by JFlex 1.9.1 http://jflex.de/  (tweaked for IntelliJ platform)
// source: RuneScript.flex

package io.runescript.plugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import io.runescript.plugin.ide.config.RsConfig;

import java.util.ArrayList;
import java.util.List;

import static io.runescript.plugin.lang.psi.RuneScriptTokenTypes.*;
import static io.runescript.plugin.lang.psi.RuneScriptTypes.*;
import io.runescript.plugin.lang.lexer.RuneScriptLexerInfo;

class _RuneScriptLexer implements FlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int STRING = 2;
  public static final int STRING_INTERPOLATION = 4;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0,  0,  1,  1,  0, 0
  };

  /**
   * Top-level table for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_TOP = zzUnpackcmap_top();

  private static final String ZZ_CMAP_TOP_PACKED_0 =
    "\1\0\u10ff\u0100";

  private static int [] zzUnpackcmap_top() {
    int [] result = new int[4352];
    int offset = 0;
    offset = zzUnpackcmap_top(ZZ_CMAP_TOP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_top(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Second-level tables for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_BLOCKS = zzUnpackcmap_blocks();

  private static final String ZZ_CMAP_BLOCKS_PACKED_0 =
    "\11\0\1\1\1\2\2\0\1\3\22\0\1\1\1\4"+
    "\1\5\1\0\1\6\1\7\1\10\1\0\1\11\1\12"+
    "\1\13\1\14\1\15\1\16\1\17\1\20\1\21\11\22"+
    "\1\23\1\24\1\25\1\26\1\27\2\0\6\30\21\17"+
    "\1\31\2\17\1\32\1\0\1\33\1\34\1\17\1\0"+
    "\1\35\1\36\1\37\1\40\1\41\1\42\1\17\1\43"+
    "\1\44\2\17\1\45\1\17\1\46\1\47\2\17\1\50"+
    "\1\51\1\52\1\53\1\17\1\54\1\31\2\17\1\55"+
    "\1\56\1\57\1\60\u0181\0";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[512];
    int offset = 0;
    offset = zzUnpackcmap_blocks(ZZ_CMAP_BLOCKS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_blocks(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7"+
    "\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17"+
    "\2\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27"+
    "\1\30\10\16\1\31\1\32\1\33\1\34\1\35\1\36"+
    "\1\37\2\0\1\16\1\40\1\41\3\16\1\42\4\16"+
    "\5\0\2\43\1\20\10\16\4\0\1\44\1\45\1\46"+
    "\2\16\1\47\1\16\1\50\1\16\1\0\1\51\1\0"+
    "\1\16\1\52\1\16\1\53\1\35\1\16\1\54\1\0"+
    "\1\55";

  private static int [] zzUnpackAction() {
    int [] result = new int[96];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\61\0\142\0\142\0\142\0\142\0\142\0\142"+
    "\0\142\0\142\0\142\0\142\0\223\0\142\0\142\0\223"+
    "\0\304\0\365\0\u0126\0\142\0\142\0\u0157\0\142\0\u0188"+
    "\0\142\0\142\0\142\0\u01b9\0\u01ea\0\u021b\0\u024c\0\u027d"+
    "\0\u02ae\0\u02df\0\u0310\0\142\0\142\0\142\0\142\0\u0341"+
    "\0\142\0\u0372\0\u03a3\0\u03d4\0\u0405\0\142\0\142\0\u0436"+
    "\0\u0467\0\u0498\0\223\0\u04c9\0\u04fa\0\u052b\0\u055c\0\u058d"+
    "\0\u05be\0\u05ef\0\u0620\0\u0651\0\142\0\u0682\0\u0405\0\u06b3"+
    "\0\u06e4\0\u0715\0\u0746\0\u0777\0\u07a8\0\u07d9\0\u080a\0\u083b"+
    "\0\u086c\0\u089d\0\u08ce\0\142\0\223\0\223\0\u08ff\0\u0930"+
    "\0\223\0\u0961\0\223\0\u0992\0\u09c3\0\142\0\u09f4\0\u0a25"+
    "\0\223\0\u0a56\0\223\0\u0a87\0\u0ab8\0\223\0\u0ae9\0\223";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[96];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length() - 1;
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpacktrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\3\4\1\5\1\6\1\7\1\10\1\11\1\12"+
    "\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22"+
    "\1\23\1\24\1\25\1\26\1\27\1\30\2\20\1\31"+
    "\1\32\1\33\2\20\1\34\1\35\1\20\1\36\1\20"+
    "\1\37\1\20\1\40\1\20\1\41\1\20\1\42\1\20"+
    "\1\43\1\44\1\45\1\46\1\47\2\50\2\3\1\50"+
    "\1\51\17\50\1\52\33\50\75\0\1\20\2\0\1\20"+
    "\1\0\2\20\5\0\2\20\3\0\20\20\17\0\1\53"+
    "\4\0\1\54\54\0\1\20\2\0\1\20\1\0\2\23"+
    "\5\0\1\20\1\55\3\0\20\20\20\0\1\20\2\0"+
    "\1\20\1\0\2\23\5\0\2\20\3\0\20\20\32\0"+
    "\1\56\60\0\1\57\46\0\1\20\2\0\1\20\1\0"+
    "\2\20\5\0\2\20\3\0\1\60\17\20\20\0\1\20"+
    "\2\0\1\20\1\0\2\20\5\0\2\20\3\0\4\20"+
    "\1\61\13\20\20\0\1\20\2\0\1\20\1\0\2\20"+
    "\5\0\2\20\3\0\1\62\17\20\20\0\1\20\2\0"+
    "\1\20\1\0\2\20\5\0\2\20\3\0\5\20\1\63"+
    "\12\20\20\0\1\20\2\0\1\20\1\0\2\20\5\0"+
    "\2\20\3\0\16\20\1\64\1\20\20\0\1\20\2\0"+
    "\1\20\1\0\2\20\5\0\2\20\3\0\4\20\1\65"+
    "\13\20\20\0\1\20\2\0\1\20\1\0\2\20\5\0"+
    "\2\20\3\0\13\20\1\66\4\20\20\0\1\20\2\0"+
    "\1\20\1\0\2\20\5\0\2\20\3\0\6\20\1\67"+
    "\11\20\4\0\2\50\2\0\1\50\1\0\17\50\1\0"+
    "\33\50\20\0\1\70\15\0\1\71\1\72\21\0\13\73"+
    "\1\74\45\73\2\54\1\75\1\76\55\54\14\0\1\20"+
    "\2\0\1\20\1\0\2\77\5\0\1\77\1\20\3\0"+
    "\6\77\12\20\20\0\1\20\2\0\1\20\1\0\2\20"+
    "\5\0\2\20\3\0\10\20\1\100\3\20\1\101\3\20"+
    "\20\0\1\20\2\0\1\20\1\0\2\20\5\0\2\20"+
    "\3\0\5\20\1\102\12\20\20\0\1\20\2\0\1\20"+
    "\1\0\2\20\5\0\2\20\3\0\10\20\1\103\7\20"+
    "\20\0\1\20\2\0\1\20\1\0\2\20\5\0\2\20"+
    "\3\0\10\20\1\104\7\20\20\0\1\20\2\0\1\20"+
    "\1\0\2\20\5\0\2\20\3\0\15\20\1\105\2\20"+
    "\20\0\1\20\2\0\1\20\1\0\2\20\5\0\2\20"+
    "\3\0\16\20\1\106\1\20\20\0\1\20\2\0\1\20"+
    "\1\0\2\20\5\0\2\20\3\0\7\20\1\107\10\20"+
    "\43\0\1\110\71\0\1\111\57\0\1\112\11\0\13\73"+
    "\1\113\45\73\13\0\1\74\4\0\1\114\42\0\1\75"+
    "\72\0\1\20\2\0\1\20\1\0\2\20\5\0\2\20"+
    "\3\0\2\20\1\115\15\20\20\0\1\20\2\0\1\20"+
    "\1\0\2\20\5\0\2\20\3\0\4\20\1\116\13\20"+
    "\20\0\1\20\2\0\1\20\1\0\2\20\5\0\2\20"+
    "\3\0\1\117\17\20\20\0\1\20\2\0\1\20\1\0"+
    "\2\20\5\0\2\20\3\0\14\20\1\120\3\20\20\0"+
    "\1\20\2\0\1\20\1\0\2\20\5\0\2\20\3\0"+
    "\10\20\1\121\7\20\20\0\1\20\2\0\1\20\1\0"+
    "\2\20\5\0\2\20\3\0\16\20\1\122\1\20\20\0"+
    "\1\20\2\0\1\20\1\0\2\20\5\0\2\20\3\0"+
    "\4\20\1\123\13\20\20\0\1\20\2\0\1\20\1\0"+
    "\2\20\5\0\2\20\3\0\10\20\1\124\7\20\53\0"+
    "\1\125\40\0\1\126\76\0\1\127\13\0\13\73\1\113"+
    "\4\73\1\114\40\73\14\0\1\20\2\0\1\20\1\0"+
    "\2\20\5\0\2\20\3\0\16\20\1\130\1\20\20\0"+
    "\1\20\2\0\1\20\1\0\2\20\5\0\2\20\3\0"+
    "\4\20\1\131\13\20\20\0\1\20\2\0\1\20\1\0"+
    "\2\20\5\0\2\20\3\0\13\20\1\132\4\20\20\0"+
    "\1\20\2\0\1\20\1\0\2\20\5\0\2\20\3\0"+
    "\4\20\1\133\13\20\51\0\1\111\41\0\1\134\46\0"+
    "\1\20\2\0\1\20\1\0\2\20\5\0\2\20\3\0"+
    "\10\20\1\135\7\20\20\0\1\20\2\0\1\20\1\0"+
    "\2\20\5\0\2\20\3\0\11\20\1\136\6\20\25\0"+
    "\2\137\5\0\1\137\4\0\6\137\32\0\1\20\2\0"+
    "\1\20\1\0\2\20\5\0\2\20\3\0\15\20\1\140"+
    "\2\20\25\0\2\137\4\0\1\126\1\137\4\0\6\137"+
    "\16\0";

  private static int [] zzUnpacktrans() {
    int [] result = new int[2842];
    int offset = 0;
    offset = zzUnpacktrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpacktrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String[] ZZ_ERROR_MSG = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state {@code aState}
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\12\11\1\1\2\11\4\1\2\11\1\1\1\11"+
    "\1\1\3\11\10\1\4\11\1\1\1\11\1\1\2\0"+
    "\1\1\2\11\10\1\5\0\1\11\12\1\4\0\1\11"+
    "\10\1\1\0\1\11\1\0\7\1\1\0\1\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[96];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** Number of newlines encountered up to the start of the matched text. */
  @SuppressWarnings("unused")
  private int yyline;

  /** Number of characters from the last newline up to the start of the matched text. */
  @SuppressWarnings("unused")
  protected int yycolumn;

  /** Number of characters up to the start of the matched text. */
  @SuppressWarnings("unused")
  private long yychar;

  /** Whether the scanner is currently at the beginning of a line. */
  @SuppressWarnings("unused")
  private boolean zzAtBOL = true;

  /** Whether the user-EOF-code has already been executed. */
  @SuppressWarnings("unused")
  private boolean zzEOFDone;

  /* user code: */
private RuneScriptLexerInfo lexerInfo;

public List<String> getTypeNames() {
    return lexerInfo.getTypeNames();
}


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  _RuneScriptLexer(java.io.Reader in, RuneScriptLexerInfo lexerInfo) {
  this.lexerInfo =  lexerInfo;
    this.zzReader = in;
  }


  /** Returns the maximum size of the scanner buffer, which limits the size of tokens. */
  private int zzMaxBufferLen() {
    return Integer.MAX_VALUE;
  }

  /**  Whether the scanner buffer can grow to accommodate a larger token. */
  private boolean zzCanGrow() {
    return true;
  }

  /**
   * Translates raw input code points to DFA table row
   */
  private static int zzCMap(int input) {
    int offset = input & 255;
    return offset == input ? ZZ_CMAP_BLOCKS[offset] : ZZ_CMAP_BLOCKS[ZZ_CMAP_TOP[input >> 8] | offset];
  }

  public final int getTokenStart() {
    return zzStartRead;
  }

  public final int getTokenEnd() {
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end, int initialState) {
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      {@code false}, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position {@code pos} from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException
  {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMap(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1:
            { return TokenType.BAD_CHARACTER;
            }
          // fall through
          case 46: break;
          case 2:
            { return TokenType.WHITE_SPACE;
            }
          // fall through
          case 47: break;
          case 3:
            { return EXCEL;
            }
          // fall through
          case 48: break;
          case 4:
            { yybegin(STRING); return STRING_START;
            }
          // fall through
          case 49: break;
          case 5:
            { return DOLLAR;
            }
          // fall through
          case 50: break;
          case 6:
            { return PERCENT;
            }
          // fall through
          case 51: break;
          case 7:
            { return AMPERSAND;
            }
          // fall through
          case 52: break;
          case 8:
            { return LPAREN;
            }
          // fall through
          case 53: break;
          case 9:
            { return RPAREN;
            }
          // fall through
          case 54: break;
          case 10:
            { return STAR;
            }
          // fall through
          case 55: break;
          case 11:
            { return PLUS;
            }
          // fall through
          case 56: break;
          case 12:
            { return COMMA;
            }
          // fall through
          case 57: break;
          case 13:
            { return MINUS;
            }
          // fall through
          case 58: break;
          case 14:
            { CharSequence lexeme = yytext();
  for (String typeName: getTypeNames()) {
      if (typeName.contentEquals(lexeme)) {
          return TYPE_NAME;
      }
       if (lexeme.length() > 4
              && lexeme.charAt(0) == 'd'
              && lexeme.charAt(1) == 'e'
              && lexeme.charAt(2) == 'f'
              && lexeme.charAt(3) == '_'
              && typeName.contentEquals(lexeme.subSequence(4, lexeme.length()))) {
          return DEFINE_TYPE;
      }
       if (lexeme.length() > 7
              && lexeme.charAt(0) == 's'
              && lexeme.charAt(1) == 'w'
              && lexeme.charAt(2) == 'i'
              && lexeme.charAt(3) == 't'
              && lexeme.charAt(4) == 'c'
              && lexeme.charAt(5) == 'h'
              && lexeme.charAt(6) == '_'
              && typeName.contentEquals(lexeme.subSequence(7, lexeme.length()))) {
          return SWITCH;
      }
       int length = typeName.length();
       if (lexeme.length() == length + 5
              && lexeme.charAt(length) == 'a'
              && lexeme.charAt(length + 1) == 'r'
              && lexeme.charAt(length + 2) == 'r'
              && lexeme.charAt(length + 3) == 'a'
              && lexeme.charAt(length + 4) == 'y'
              && typeName.contentEquals(lexeme.subSequence(0, length))) {
          return ARRAY_TYPE_NAME;
      }
  }
  return IDENTIFIER;
            }
          // fall through
          case 59: break;
          case 15:
            { return SLASH;
            }
          // fall through
          case 60: break;
          case 16:
            { return INTEGER;
            }
          // fall through
          case 61: break;
          case 17:
            { return COLON;
            }
          // fall through
          case 62: break;
          case 18:
            { return SEMICOLON;
            }
          // fall through
          case 63: break;
          case 19:
            { return LT;
            }
          // fall through
          case 64: break;
          case 20:
            { return EQUAL;
            }
          // fall through
          case 65: break;
          case 21:
            { if (yystate() == STRING_INTERPOLATION) {
        yybegin(STRING);
        return STRING_INTERPOLATION_END;
    } else {
        return GT;
    }
            }
          // fall through
          case 66: break;
          case 22:
            { return LBRACKET;
            }
          // fall through
          case 67: break;
          case 23:
            { return RBRACKET;
            }
          // fall through
          case 68: break;
          case 24:
            { return CARET;
            }
          // fall through
          case 69: break;
          case 25:
            { return LBRACE;
            }
          // fall through
          case 70: break;
          case 26:
            { return BAR;
            }
          // fall through
          case 71: break;
          case 27:
            { return RBRACE;
            }
          // fall through
          case 72: break;
          case 28:
            { return TILDE;
            }
          // fall through
          case 73: break;
          case 29:
            { return STRING_PART;
            }
          // fall through
          case 74: break;
          case 30:
            { yybegin(YYINITIAL); return STRING_END;
            }
          // fall through
          case 75: break;
          case 31:
            { yybegin(STRING_INTERPOLATION); return STRING_INTERPOLATION_START;
            }
          // fall through
          case 76: break;
          case 32:
            { return LTE;
            }
          // fall through
          case 77: break;
          case 33:
            { return GTE;
            }
          // fall through
          case 78: break;
          case 34:
            { return IF;
            }
          // fall through
          case 79: break;
          case 35:
            { return SINGLE_LINE_COMMENT;
            }
          // fall through
          case 80: break;
          case 36:
            { return MULTI_LINE_COMMENT;
            }
          // fall through
          case 81: break;
          case 37:
            { return CALC;
            }
          // fall through
          case 82: break;
          case 38:
            { return CASE;
            }
          // fall through
          case 83: break;
          case 39:
            { return NULL;
            }
          // fall through
          case 84: break;
          case 40:
            { return TRUE;
            }
          // fall through
          case 85: break;
          case 41:
            { return STRING_TAG;
            }
          // fall through
          case 86: break;
          case 42:
            { return FALSE;
            }
          // fall through
          case 87: break;
          case 43:
            { return WHILE;
            }
          // fall through
          case 88: break;
          case 44:
            { return RETURN;
            }
          // fall through
          case 89: break;
          case 45:
            { return DEFAULT;
            }
          // fall through
          case 90: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}