import java.util.ArrayList;
import java.util.List;

public class SimpleASTNode implements  ASTNode {
    List<ASTNode> children = new ArrayList<>();
    ASTNodeType nodeType = null;
    String text = null;
    SimpleASTNode(ASTNodeType nodeType,String text){
        this.nodeType = nodeType;
        this.text = text;
    }
    @Override
    public List<ASTNode> getChildren() {
        return children;
    }

    @Override
    public ASTNodeType getType() {
        return nodeType;
    }

    @Override
    public String getText() {
        return text;
    }

    public void addChild(ASTNode a){
        children.add(a);
    }
}
