# LuoCache
this is a easy cache util for your project
    
一款简单的使用apt实现的缓存处理器,默认使用gson+sp实现，只需要编写实体类，添加注解即可自动生成缓存代码，无需做任何额外操作。

    
依赖地址

      maven { url 'https://jitpack.io' }
    
      implementation 'com.github.wxxewx.LuoCache:luocache:v0.3'
      annotationProcessor 'com.github.wxxewx.LuoCache:luocache-processor:v0.3'
      
如何使用
    
  第一步，编写您需要缓存的实体类
    
    @LuoCache
    public class User {
        String name;
        String city;
        int age;
        Account account;
    }

  第二步：build
  
  第三步： build之后会生成您的实体类名称+manage的单例
  
    public class UserManager {
        ...
    }
    
  第四步：初始化
    
    //使用默认的缓存策略进行缓存（gson+sp）
    LuoCache.init(getApplication());
    
    //使用自定义的缓存策略
    LuoCache.init(getApplication(), SpStrategy.class);
    
   如何自定义: 实现IStrategy接口即可
   
    public class MyCache implements IStrategy {
        @Override
        public void initCache(Application context, String cacheName) {
    
        }
    
        @Override
        public Object getCache(String key, Class type) {
            return null;
        }
    
        @Override
        public boolean setCache(String key, Object value) {
            return false;
        }
    
        @Override
        public boolean removeCache(String key) {
            return false;
        }
    }
  第五步:设置缓存内容
  
     UserManager.getInstance().setName(name);
     UserManager.getInstance().setAge(Integer.parseInt(age));
     UserManager.getInstance().setCity(city);
     ...
     
  第六步:获取缓存内容
  
     String name = UserManager.getInstance().getName();
     Account account = UserManager.getInstance().getAccount();
     int age = UserManager.getInstance().getAge();
     
  第七步:清除缓存
  
     UserManager.getInstance().removeName(); //清除单个配置字段
     UserManager.getInstance().clear();     //清空
     
     
  给单个的缓存实体配置不同的缓存策略
  
     UserManager.getInstance().setStrategy(new SpStrategy());
