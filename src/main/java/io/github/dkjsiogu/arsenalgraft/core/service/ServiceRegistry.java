package io.github.dkjsiogu.arsenalgraft.core.service;

import io.github.dkjsiogu.arsenalgraft.ArsenalGraft;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.ModificationManager;
import io.github.dkjsiogu.arsenalgraft.api.v3.modification.impl.ModificationManagerImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 线程安全的服务注册中心
 * 
 * 替代单例模式，提供依赖注入和服务定位功能
 * 支持多线程环境下的服务管理
 */
public class ServiceRegistry {
    
    private static final ServiceRegistry INSTANCE = new ServiceRegistry();
    
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    private ServiceRegistry() {
        // 私有构造函数，确保单例
    }
    
    public static ServiceRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * 注册服务
     */
    public <T> void registerService(Class<T> serviceClass, T implementation) {
        lock.writeLock().lock();
        try {
            if (services.containsKey(serviceClass)) {
                throw new IllegalStateException("Service already registered: " + serviceClass.getName());
            }
            services.put(serviceClass, implementation);
            ArsenalGraft.LOGGER.info("注册服务: {}", serviceClass.getSimpleName());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 获取服务
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        lock.readLock().lock();
        try {
            T service = (T) services.get(serviceClass);
            if (service == null) {
                throw new IllegalStateException("Service not registered: " + serviceClass.getName());
            }
            return service;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 检查服务是否已注册
     */
    public boolean isServiceRegistered(Class<?> serviceClass) {
        lock.readLock().lock();
        try {
            return services.containsKey(serviceClass);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 注销服务
     */
    public <T> void unregisterService(Class<T> serviceClass) {
        lock.writeLock().lock();
        try {
            Object removed = services.remove(serviceClass);
            if (removed != null) {
                System.out.println("[ServiceRegistry] 注销服务: " + serviceClass.getSimpleName());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 初始化核心服务
     */
    public void initializeCoreServices() {
        lock.writeLock().lock();
        try {
            if (!isServiceRegistered(ModificationManager.class)) {
                // 创建线程安全的修改管理器实例
                ModificationManagerImpl manager = new ModificationManagerImpl();
                registerService(ModificationManager.class, manager);
                System.out.println("[ServiceRegistry] 初始化核心服务完成");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 清理所有服务（用于模组卸载）
     */
    public void clearAllServices() {
        lock.writeLock().lock();
        try {
            services.clear();
            System.out.println("[ServiceRegistry] 清理所有服务");
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // 便捷方法
    public static ModificationManager getModificationManager() {
        return getInstance().getService(ModificationManager.class);
    }
}
