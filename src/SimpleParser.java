import java.io.IOException;

public class SimpleParser {
    public static void main(String[] args) {

    }

    public SimpleASTNode parse(String script) throws Exception {
        SimpleLexer lexer = new SimpleLexer();
        SimpleParser parser = new SimpleParser();
        return parseNode(lexer.tokenize(script));
    }

    public void dumpAST(ASTNode node,String indent){
        System.out.println(indent + node.getType() + " " + node.getText());
        for (ASTNode child : node.getChildren()) {
            dumpAST(child,indent+"\t");
        }
    }

    public SimpleASTNode parseNode(TokenReader tokens) throws Exception {
        SimpleASTNode node = new SimpleASTNode(ASTNodeType.Program,"begin");
        while(tokens.peek()!=null){
            SimpleASTNode child = intDeclare(tokens);
            if(child==null){
                child = assigmentStatement(tokens);
            }
            if(child==null){
                child = expressionStatement(tokens);
            }
            if(child!=null){
                node.addChild(child);
            }else{
                throw new Exception("unknown statement");
            }
        }
        return  node;
    }
    public SimpleASTNode expressionStatement(TokenReader tokens) throws Exception {
        int p = tokens.getPosition();
        SimpleASTNode node = additive(tokens);
        if(node!=null){
            Token token = tokens.peek();
            if(token!=null&&token.getType()==TokenType.SemiColon){
                tokens.read();
            }else{
                tokens.setPosition(p);
                node = null;
            }
        }
        return node;
    }
    public SimpleASTNode assigmentStatement(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();
        if(token!=null&&token.getType()==TokenType.Identifier){
            token = tokens.read();
            node = new SimpleASTNode(ASTNodeType.AssignmentStmt,token.getText());
            token = tokens.peek();
            if(token!=null&&token.getType()==TokenType.EQ){
                token = tokens.read();
                SimpleASTNode child = additive(tokens);
                if(child!=null){
                    node.addChild(child);
                    token = tokens.peek();
                    if (token != null && token.getType() == TokenType.SemiColon) {
                        tokens.read();

                    } else {
                        throw new Exception("invalid statement, expecting semicolon");
                    }
                }else{
                    throw new Exception("invalide assignment statment,expecting an expression");
                }
            }else{
                tokens.unread();
                node = null;
            }
        }
        return node;
    }

    public SimpleASTNode intDeclare(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();
        if(token!=null&&token.getType()==TokenType.Int){
            token = tokens.read();
            if(tokens.peek()!=null&&tokens.peek().getType()==TokenType.Identifier){
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.IntDeclaration,token.getText());
                if(tokens.peek()!=null&&tokens.peek().getType()==TokenType.EQ){
                    token = tokens.read();
                    SimpleASTNode child = additive(tokens);
                    if(child!=null){
                        node.addChild(child);
                    }else{
                        throw new Exception("invalide variable initialization, expecting an expression");
                    }
                }
            }else{
                throw new Exception("variable name expected");
            }
            if(node!=null){
                token = tokens.peek();
                if(token!=null&&token.getType()==TokenType.SemiColon){
                    tokens.read();
                }else{
                    throw new Exception("invalid statement expecting semicolon");
                }
            }
        }
        return node;
    }

    public SimpleASTNode additive(TokenReader tokens) throws Exception {
        SimpleASTNode child1 = multiplication(tokens);
        SimpleASTNode node = child1;
        if(child1!=null){
            while(true){
                Token token = tokens.peek();
                if(token!=null&&(token.getType()==TokenType.Plus||token.getType()==TokenType.Minus)){
                    token = tokens.read();
                    SimpleASTNode child2 = multiplication(tokens);
                    if(child2!=null){
                        node = new SimpleASTNode(ASTNodeType.Additive,token.getText());
                        node.addChild(child1);
                        node.addChild(child2);
                        child1 = node;
                    }else{
                        throw  new Exception("invalid additive expression, expecting the right part.");
                    }
                }else{
                    break;
                }

            }
        }
        return node;

    }

    public SimpleASTNode multiplication(TokenReader tokens) throws Exception {
        SimpleASTNode child1 = primary(tokens);
        SimpleASTNode node = child1;
        if(child1!=null){
            while (true){
                Token token = tokens.peek();
                if(token!=null&&(token.getType()==TokenType.Mul||token.getType()==TokenType.Div)){
                    token = tokens.read();
                    SimpleASTNode child2 = primary(tokens);
                    if(child2!=null){
                        node = new SimpleASTNode(ASTNodeType.Multiplicative,token.getText());
                        node.addChild(child1);
                        node.addChild(child2);
                        child1 = node;
                    }else{
                        throw new Exception("invalid expression expecting the right part.");
                    }
                }else{
                    break;
                }
            }
        }
        return node;
    }

    public SimpleASTNode primary(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();
        if(token!=null){
            if(token.getType()==TokenType.IntLiteral){
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.IntLiteral,token.getText());
            }else if(token.getType()==TokenType.Identifier){
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.Identifier,token.getText());

            }else if(token.getType()==TokenType.LeftParen){
                token = tokens.read();
                node = additive(tokens);
                if(node!=null){
                    token = tokens.peek();
                    if(token!=null&&token.getType()==TokenType.RightParen){
                        tokens.read();
                    }else{
                        throw new Exception("expecting a right parenthesis");
                    }
                }else{
                    throw new Exception("expecting an additive expressing inside parenthesis");
                }
            }
        }
        return node;
    }

}
