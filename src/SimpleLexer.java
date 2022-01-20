import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleLexer {
    private List<Token> tokens = null;
    private StringBuffer tokenText = null;
    private SimpleToken token;

    private final class SimpleToken implements Token{
        private TokenType tokenType;
        private String text;

        @Override
        public TokenType getType() {
            return tokenType;
        }

        @Override
        public String getText() {
            return text;
        }
    }

    private enum DfaStatus{
        //起始符
        Initial,

        Int,  //int关键字
        Int1,
        Int2,
        Int3,
//        IF,     //if关键字
//        IF1,
//        IF2,
//        Char,   //char关键字
//        Char1,
//        Char2,
//        Char3,
//        Char4,
//        Long,   //long关键字
//        Long1,
//        Long2,
//        Long3,
//        Long4,
//        Double,     //double关键字
//        Float,  //float关键字
//        While,  //while关键字
//        Do,  //do关键字

        //运算符
        Plus, //+
        Minus,  //-
        Mul,  //*
        Div, //%
//        Auto_inc,//++
//        Auto_sub,//--
        AND, //&&
        OR, //||
        NOT, //^
        GE, //>=
        GT, //>
        LE, //<=
        LT, //<
        EQ, //==

        //界符
        SemiColon, // ;
        LeftParen, // (
        RightParen, // )
//        LeftBigParen, // {
//        RightBigParen, // }

        //标识符
        Identifier,

        //常数
        IntLiteral, //int字面量
//        LongLiteral, //long字面量
//        DoubleLiteral, //double字面量
//        StringLiteral, //string字面量
    }

    public boolean isAlpha(char c){
        return (c>='a'&&c<='z')||(c>='A'&&c<='Z');
    }

    public boolean isDigit(char c){
        return c>='0'&&c<='9';
    }
    public TokenReader tokenize(String code) throws IOException {
        tokens = new ArrayList<>();
        token = new SimpleToken();
        tokenText = new StringBuffer();
        CharArrayReader reader = new CharArrayReader(code.toCharArray());
        int ich = 0;
        char ch = 0;
        DfaStatus status = DfaStatus.Initial;
        while((ich=reader.read())!=-1){
            ch = (char) ich;
            switch (status){
                case Initial:
                    status = initToken(ch);
                    break;
                case Int1:
                    if (ch == 'n') {
                        status = DfaStatus.Int2;
                        tokenText.append(ch);
                    } else if (isDigit(ch) || isAlpha(ch)) {
                        status = DfaStatus.Identifier;
                        tokenText.append(ch);
                    } else {
                        status = initToken(ch);
                    }
                    break;
                case Int2:
                    if (ch == 't') {
                        status = DfaStatus.Int3;
                        tokenText.append(ch);
                    } else if (isDigit(ch) || isAlpha(ch)) {
                        status = DfaStatus.Identifier;
                        tokenText.append(ch);
                    } else {
                        status = initToken(ch);
                    }
                    break;
                case Int3:
                    if (isDigit(ch) || isAlpha(ch)) {
                        status = DfaStatus.Identifier;
                        tokenText.append(ch);
                    } else {
                        token.tokenType = TokenType.Int;
                        status = initToken(ch);
                    }
                    break;
                case Identifier:
                    if (isDigit(ch) || isAlpha(ch)) {
                        tokenText.append(ch);
                    } else {
                        status = initToken(ch);
                    }
                    break;
                case IntLiteral:
                    if (isDigit(ch)) {
                        tokenText.append(ch);
                    } else {
                        status = initToken(ch);
                    }
                    break;
                case GE:
                case LE:
                case EQ:
                case Plus:
                case Minus:
                case Div:
                case Mul:
                case SemiColon:
                case LeftParen:
                case RightParen:
                    status = initToken(ch);
                    break;
                case GT:
                    if (ch == '=') {
                        status = DfaStatus.GE;
                        token.tokenType = TokenType.GE;
                        tokenText.append(ch);
                    } else {
                        status = initToken(ch);
                    }
                    break;
                case LT:
                    if (ch == '=') {
                        status = DfaStatus.LE;
                        token.tokenType = TokenType.LE;
                        tokenText.append(ch);
                    } else {
                        status = initToken(ch);
                    }
                    break;
            }
        }
        initToken(ch);
        return new SimpleTokenReader(tokens);
    }

    public final class SimpleTokenReader implements TokenReader{

        private List<Token> list;
        private int position=0;
        private int size = 0;

        public SimpleTokenReader(List<Token> tokens) {
            list = tokens;
            size = tokens.size();
        }

        public boolean check(int position) {
            if (position<size&&position>=0) return true;
            return false;
        }

        @Override
        public Token read() {
            if (check(position)) {
                return list.get(position++);
            }
            return null;
        }

        @Override
        public Token peek() {
            if (check(position)) {
                return list.get(position);
            }
            return null;
        }

        @Override
        public void unread() {
            if (check(position - 1)) {
                position--;
            }
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public void setPosition(int position) {
            this.position = position;
        }
    }
    public DfaStatus initToken(char ch){
        if(tokenText.length()!=0){
            Token t = new SimpleToken();
            token.text = tokenText.toString();
            tokens.add(token);
            token = new SimpleToken();
            tokenText = new StringBuffer();
        }
        DfaStatus status = DfaStatus.Initial;
        if (isAlpha(ch)){
            if (ch=='i'){
                status = DfaStatus.Int1;
            }else{
                status = DfaStatus.Identifier;
            }
            tokenText.append(ch);
            token.tokenType = TokenType.Identifier;
        } else if (isDigit(ch)) {
            status = DfaStatus.IntLiteral;
            token.tokenType = TokenType.IntLiteral;
            tokenText.append(ch);
        } else if (ch == '+') {
            status = DfaStatus.Plus;
            token.tokenType = TokenType.Plus;
            tokenText.append(ch);
        } else if (ch == '-') {
            status = DfaStatus.Minus;
            token.tokenType = TokenType.Minus;
            tokenText.append(ch);
        }else if(ch =='*'){
            status = DfaStatus.Mul;
            token.tokenType = TokenType.Mul;
            tokenText.append(ch);
        } else if (ch == '/') {
            status = DfaStatus.Div;
            token.tokenType = TokenType.Div;
            tokenText.append(ch);
        } else if (ch == '=') {
            status = DfaStatus.EQ;
            token.tokenType = TokenType.EQ;
            tokenText.append(ch);
        } else if (ch == '<') {
            status = DfaStatus.LT;
            token.tokenType = TokenType.LT;
            tokenText.append(ch);
        } else if (ch == '>') {
            status = DfaStatus.GT;
            token.tokenType = TokenType.GT;
            tokenText.append(ch);
        } else if (ch == '(') {
            status = DfaStatus.LeftParen;
            token.tokenType = TokenType.LeftParen;
            tokenText.append(ch);
        } else if (ch == ')') {
            status = DfaStatus.RightParen;
            token.tokenType = TokenType.RightParen;
            tokenText.append(ch);
        } else if (ch == ';') {
            status = DfaStatus.SemiColon;
            token.tokenType = TokenType.SemiColon;
            tokenText.append(ch);
        } else {
            status = DfaStatus.Initial;
        }
        return status;
    }

    public static void dump(TokenReader tokenReader){
        System.out.println("text\ttype");
        Token token = null;
        while ((token= tokenReader.read())!=null){
            System.out.println(token.getText()+"\t\t"+token.getType());
        }
    }

    public static void main(String[] args) {
        SimpleLexer lexer = new SimpleLexer();

        String script = "int age = 45);";
        System.out.println("parse :" + script);
        TokenReader tokenReader = null;
        try {
            tokenReader = lexer.tokenize(script);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dump(tokenReader);
    }
}
