//观察者
public interface Observer {
    public void update();
}

//被观察者
abstract public class Subject {

    private List<Observer> observerList = new ArrayList<Observer>();

    public void attachObserver(Observer observer) {
        observerList.add(observer);
    }

    public void detachObserver(Observer observer){
        observerList.remove(observer);
    }

    public void notifyObservers(){
        for (Observer observer: observerList){
            observer.update();
        }
    }
}

//怪物
public class Monster implements Observer {

    @Override
    public void update() {
        if(inRange()){
            System.out.println("怪物 对主角攻击！");
        }
    }

    private boolean inRange(){
        //判断主角是否在自己的影响范围内，这里忽略细节，直接返回true
        return true;
    }
}

//陷阱
public class Trap implements Observer {

    @Override
    public void update() {
        if(inRange()){
            System.out.println("陷阱 困住主角！");
        }
    }

    private boolean inRange(){
        //判断主角是否在自己的影响范围内，这里忽略细节，直接返回true
        return true;
    }
}

//宝物
public class Treasure implements Observer {

    @Override
    public void update() {
        if(inRange()){
            System.out.println("宝物 为主角加血！");
        }
    }

    private boolean inRange(){
        //判断主角是否在自己的影响范围内，这里忽略细节，直接返回true
        return true;
    }
}

public class Client {
    public static void main(String[] args) {
        //初始化对象
        Hero hero = new Hero();
        Monster monster = new Monster();
        Trap trap = new Trap();
        Treasure treasure = new Treasure();
        //注册观察者
        hero.attachObserver(monster);
        hero.attachObserver(trap);
        hero.attachObserver(treasure);
        //移动事件
        hero.move();
    }
}