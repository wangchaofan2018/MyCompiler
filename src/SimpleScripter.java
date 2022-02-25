import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class SimpleScripter {
    private HashMap<String, Integer> variables = new HashMap<>();
    private static boolean verbose;

    public static void main(String[] args) throws Exception {
        SimpleParser parser = new SimpleParser();
        if(args.length>0&&args[0].equals("-v")){
            verbose = true;
            System.out.println("Verbose mode");
        }
        System.out.println("Simple script language");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        SimpleScripter scripter = new SimpleScripter();
        String script = "";
        System.out.print("\n>");
        while(true){
            String line = reader.readLine().trim();
            if(line.equals("exit();")){
                System.out.println("good bye!");
                break;
            }
            script+=line + "\n"; //为什么加换行
            if(line.endsWith(";")){
                ASTNode node = parser.parse(script);
                if(verbose){
                    parser.dumpAST(node,"");
                }
                scripter.evaluate(node,"");
                System.out.print("\n>");
                script = "";
            }

        }


    }

    public Integer evaluate(ASTNode node,String indent) throws Exception {
        if(verbose){
            System.out.println(indent+"Calculating: "+node.getType());
        }
        Integer result = null;
        String raw = indent;
        switch (node.getType()){
            case Program:
                for(ASTNode n:node.getChildren()){
                    result = evaluate(n,indent);
                }break;
            case Additive:
                ASTNode child1 = node.getChildren().get(0);
                Integer value1 = evaluate(child1,indent+"\t");
                ASTNode child2 = node.getChildren().get(1);
                Integer value2 = evaluate(child2, indent+"\t");
                if(node.getText().equals("+")){
                    result =  value1+value2;
                }else{
                    result= value1-value2;
                }
                break;
            case Multiplicative:
                child1 = node.getChildren().get(0);
                value1 = evaluate(child1,indent+"\t");
                child2 = node.getChildren().get(1);
                value2 = evaluate(child2, indent+"\t");
                if(node.getText().equals("*")){
                    result = value1*value2;
                }else{
                    result =  value1/value2;
                }
                break;

            case IntLiteral:
                result = Integer.valueOf(node.getText()).intValue();
                break;
            case Identifier:
                String cur = node.getText();
                if(variables.containsKey(cur)){
                    Integer value = variables.get(cur);
                    if (value != null) {
                        result = value.intValue();
                    }else{
                        throw new Exception("variable " + cur + " has not been set any value");
                    }
                }else{
                    throw new Exception("unknown variables");
                }break;
            case AssignmentStmt:
                cur = node.getText();
                if(!variables.containsKey(cur)){
                    throw new Exception("unknown variable");
                }
            case IntDeclaration:
                cur = node.getText();
                if(node.getChildren().size()!=0){
                    result = evaluate(node.getChildren().get(0), indent+"\t");
                }
                variables.put(cur, result);
                break;

        }
        if(verbose){
            System.out.println(indent + " result: " + result);

        } else if (indent.equals("")) {
            if (node.getType() == ASTNodeType.IntDeclaration || node.getType() == ASTNodeType.AssignmentStmt) {
                System.out.println(node.getText() + ": " + result);
            }else if (node.getType() != ASTNodeType.Program){
                System.out.println(result);
            }
        }
        return result;
    }
}
