//定义策略接口
public interface DealStrategy{
   void dealMythod(String option);
}

//定义具体的策略1
public class DealSina implements DealStrategy{
   @override
   public void dealMythod(String option){
       //...
  }
}

//定义具体的策略2
public class DealWeChat implements DealStrategy{
   @override
   public void dealMythod(String option){
       //...
  }
}

//定义上下文，负责使用DealStrategy角色
public static class DealContext{
   private String type;
   private DealStrategy deal;
   public  DealContext(String type, DealStrategy deal){
       this.type = type;
       this.deal = deal;
   }
   public DealStrategy getDeal(){
       return deal;
   }
   public boolean options(String type){
       return this.type.equals(type);
   }
}

public class Share{
    private static List<DealContext> algs = new ArrayList();
    //静态代码块,先加载所有的策略
    static {
        algs.add(new DealContext("Sina", new DealSina()));
        algs.add(new DealContext("WeChat", new DealWeChat()));
    }
    public void shareOptions(String type){
        DealStrategy dealStrategy = null;
        for (DealContext deal : algs) {
           if (deal.options(type)) {
               dealStrategy = deal.getDeal();
               break;
            }  
        }
        dealStrategy.dealMythod(type);
    }
}


