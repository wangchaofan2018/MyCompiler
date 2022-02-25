import java.util.List;

public interface ASTNode {
    public List<ASTNode> getChildren(); //子节点
    public ASTNodeType getType(); //AST类型
    public String getText(); //文本值
}
