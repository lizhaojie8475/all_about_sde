public class Singleton {
    private Singleton() {}  //私有构造函数
    private static Singleton instance = null;  //单例对象
    //静态工厂方法
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

//这种写法只能单线程有效，也就是代码逻辑必须严格遵循串行逻辑才能正常实现两个单例的目标：
//1. 永远不会存在两个地址不同的Singleton对象
//2. 唯一的这个Singleton对象自创建起（当他不为null那一刻起）就必须是一个完整正常的对象。

/*
多线程会导致的第一个问题：
1. 多线程同时创建对象。这个问题一眼可见。就是两个线程同时运行到第6行的判断语句时，两个线程都判断为true。那么显然instance对象被创建了两次。在最极端的情况下，线程AB同时到达第6行，然后线程A瞬间完成了
第7行的创建并返回了。线程B在线程A返回后才开始执行创建，然后返回。这样虽然最终instance被覆盖了，但是AB的调用方却获得了两个地址不同的对象。

针对这个问题可以作出一下修改：
*/

public class Singleton {
    private Singleton() {}  //私有构造函数
    private static Singleton instance = null;  //单例对象
   //静态工厂方法
    public static Singleton getInstance() {
        if (instance == null) {      //双重检测机制
            synchronized (Singleton.class){  //同步锁
                if (instance == null) {     //双重检测机制
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

//添加线程锁，第一层30行的判断后，会用线程锁阻塞在31行，这样就只有一个线程可以进入下边的创建语句。由于之后被卡在31行的其他线程也有可能再进入下边的语句，所以必须进行第二次判断。

/*
这种做法会导致多线程的第二个问题，那就是虽然可以保证只创建一个全局唯一的singleton对象。但是这个对象不能保证是完整的。这是由于指令重排造成的。instance = new Singleton()这条语句在底层是由三条语句组成的：
分配内存空间（空的）， 初始化对象（把内容填在里边）， 把instance的指针指向这个内存空间。而只有当第三步执行完成后instance == null这个判断才会为false。所以如果严格按照这个顺序的化，是符合线程安全的。
但是问题就在于，编译器在底层有可能会将第二三步的顺序颠倒。这样instance就会在第二步的时候 ！= null。

让我们看看这会有什么问题，线程A先进入创建语句，并且执行完了创建语句的前两步，在这个瞬间，线程B来了，进行第一层检测instance 是否为 null。由于线程A完成了前两步，所以判断为false。线程B瞬间就结束了整个函数，
把instance给返回了。更极端的情况，线程B已经把instance给返回给调用方使用了，线程A还没完成第三小步。则线程B的调用方就会无法取到初始化的内容。

针对这个问题的解决办法：添加volatile关键词。
*/

public class Singleton {
    private Singleton() {}  //私有构造函数
    private volatile static Singleton instance = null;  //单例对象
    //静态工厂方法
    public static Singleton getInstance() {
          if (instance == null) {      //双重检测机制
         synchronized (Singleton.class){  //同步锁
           if (instance == null) {     //双重检测机制
             instance = new Singleton();
                }
             }
          }
          return instance;
      }
}
// volatile关键词的作用就是，告诉编译器，被该词修饰的变量是易变的，也就是随时都有可能改变。因此要求编译器或者cpu不能对他做任何优化，首先不能去缓存或者是寄存器读取这个变量值，每次都得去主存从新读取。并且
// 不会发生指令重排。

// 对于java来说，到这里就算是可以了。但是对于go语言来说，还要考虑一个原子性的问题。也就是go语言的读取和赋值操作全都不是原子性的，换句话说就是一个单独的赋值操作可能会被分成两步来实现，先赋值前一半，再赋值
后一半。这样的话在两次赋值之间就有可能导致和上边同样的问题，也就是线程A只完成了一半的初始化，线程B就把这个对象拿走，去用了。

这里针对go语言，单独写一种可以保证原子性的操作：
type Singleton struct{}

var (
   instancev *Singleton
   lock     sync.RWMutex
)

func GetInstance() *Singleton {
   lock.RLock()
   ins := instance
   lock.RUnlock()
   if ins == nil {
      lock.Lock()
      defer lock.Unlock()
      if instance == nil {
         instance = &Singleton{}
      }
   }
   return instance
}

可以看到，这里用了一个读写锁，任何一个线程想要把instance实例读取走的话，就必须用读锁来阻塞，这样就可以保证，如果有另外一个线程在创建变量的话（写过程），读操作是不能直接结束的。87行之后的部分就完全和上边
的java程序一样采用了一个双重检测的技巧。第一层采用读锁也保证了尽量提供效率。


当然了，java自然也能发挥java自己的特色，java的特色就是内部私有类。java可以在一个类内部再创建一个类。
public class Singleton {
    private static class LazyHolder {
        private static final Singleton INSTANCE = new Singleton();
    }
    private Singleton (){}
    public static Singleton getInstance() {
        return LazyHolder.INSTANCE;
    }
}

这种方式之所以线程安全是因为系统默认为类的构造器实现了加锁和同步的所有操作。

最终提醒一下。以上所有操作都可以利用java的反射机制给打破：
//获得构造器
Constructor con = Singleton.class.getDeclaredConstructor();
//设置为可访问
con.setAccessible(true);
//构造两个不同的对象
Singleton singleton1 = (Singleton)con.newInstance();
Singleton singleton2 = (Singleton)con.newInstance();
//验证是否是不同对象
System.out.println(singleton1.equals(singleton2));

要想再解决这个问题。还可以用java的枚举类型来实现单例：
public enum SingletonEnum {
    INSTANCE;
}
 