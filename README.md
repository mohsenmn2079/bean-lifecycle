
# Spring Bean Lifecycle & Proxy Creation

This document explains the **complete lifecycle of a Bean in Spring** and where **proxy creation happens** in the process.
Understanding this lifecycle is essential before reading the implementation in this repository.

This project builds a **mini version of Spring's `@Transactional` mechanism**, and that implementation relies heavily on how Spring processes Beans internally.

---

# High Level Lifecycle

At a high level, Spring processes beans in the following order:

1. Load Bean Definitions
2. Run BeanFactoryPostProcessors
3. Register BeanPostProcessors
4. Instantiate Beans
5. Dependency Injection
6. Aware callbacks
7. BeanPostProcessor (Before Initialization)
8. Initialization
9. BeanPostProcessor (After Initialization) → **Proxy Creation happens here**
10. Bean is stored in ApplicationContext
11. Bean is used by the application
12. Bean destruction during shutdown

---

# 1. Loading Bean Definitions

During application startup, Spring scans the classpath and loads bean definitions.

Examples:

```
@Component
@Service
@Repository
@Configuration
@Bean
```

Spring does **not create objects yet**.Instead it creates **BeanDefinition metadata**, which contains information like:

- Bean class
- Scope
- Dependencies
- Initialization methods
- Lazy configuration

Example:

```
OrderServiceImpl
MiniTransactionalBeanPostProcessor
MiniTransactionManager
```

These are registered inside the **BeanFactory**.

---

# 2. BeanFactoryPostProcessor Phase

Before any beans are created, Spring executes **BeanFactoryPostProcessors**.

These components can **modify BeanDefinitions before instantiation**.

Example responsibilities:

- modifying bean configuration
- registering additional beans
- processing configuration annotations

Example:

```
ConfigurationClassPostProcessor
PropertySourcesPlaceholderConfigurer
```

At this stage:

```
Beans are NOT created yet
```

Only their **definitions** are manipulated.

---

# 3. BeanPostProcessor Registration

Next, Spring creates and registers all **BeanPostProcessors**.

BeanPostProcessors are extremely important because they can **intercept bean creation**.

Many core Spring features rely on this mechanism:

- AOP
- Transactions
- Security
- Async methods
- Caching

Example processors:

```
AnnotationAwareAspectJAutoProxyCreator
AutowiredAnnotationBeanPostProcessor
CommonAnnotationBeanPostProcessor
```

In this project we implement our own:

```
MiniTransactionalBeanPostProcessor
```

---

# 4. Bean Instantiation

Spring now begins creating beans.

For example:

```
new OrderServiceImpl()
```

At this moment:

- Constructor is executed
- Object is created
- Dependencies are not injected yet

---

# 5. Dependency Injection

Spring injects dependencies into the bean.

Examples:

```
@Autowired
@Inject
@Value
```

Fields, setters, or constructors receive their required dependencies.

---

# 6. Aware Interfaces

If the bean implements certain **Aware interfaces**, Spring calls them.

Examples:

```
BeanNameAware
BeanFactoryAware
ApplicationContextAware
EnvironmentAware
```

These allow beans to access container infrastructure.

Example:

```
setApplicationContext(...)
```

---

# 7. BeanPostProcessor (Before Initialization)

Spring now executes:

```
postProcessBeforeInitialization()
```

for every registered BeanPostProcessor.

This allows modification before initialization logic runs.

---

# 8. Initialization Phase

Now initialization logic executes.

Possible mechanisms include:

```
@PostConstruct
InitializingBean.afterPropertiesSet()
init-method
```

Example:

```
@PostConstruct
public void init() {
}
```

---

# 9. BeanPostProcessor (After Initialization)

This is **the most important stage for AOP and transactions**.

Spring calls:

```
postProcessAfterInitialization()
```

Here frameworks may **replace the bean with a proxy**.

Example logic:

```
if(bean has transactional annotation)
    return proxy(bean)
```

If a proxy is created:

```
Proxy replaces the original bean in ApplicationContext
```

From this point forward:

```
ApplicationContext stores the proxy
NOT the original object
```

This is exactly how **Spring AOP and @Transactional work**.

---

# 10. Bean Stored in ApplicationContext

After all processing is finished, the final bean instance is stored inside the container.

This might be:

```
Original Bean
OR
Proxy Bean
```

For transactional services it is usually a **proxy**.

---

# 11. Bean Usage

When the application retrieves the bean:

```
OrderService service = context.getBean(OrderService.class);
```

it actually receives:

```
Proxy(OrderServiceImpl)
```

When a method is called:

```
service.placeOrder()
```

the call flow becomes:

```
Client
 ↓
Proxy
 ↓
Interceptor
 ↓
Target Bean
```

This allows features like:

- transactions
- logging
- security checks
- caching
- retry mechanisms

---

# 12. Bean Destruction

When the application shuts down, Spring destroys beans.

Possible mechanisms:

```
@PreDestroy
DisposableBean.destroy()
destroy-method
```

Example:

```
@PreDestroy
public void cleanup() {
}
```

---

# Where Proxy Creation Happens

Proxy creation occurs during:

```
BeanPostProcessor.postProcessAfterInitialization()
```

This stage allows frameworks to wrap beans with proxies that intercept method calls.

Typical Spring components responsible for this include:

```
AnnotationAwareAspectJAutoProxyCreator
InfrastructureAdvisorAutoProxyCreator
```

In this project, the proxy is created by:

```
MiniTransactionalBeanPostProcessor
```

---

# Why Understanding This Matters

Understanding this lifecycle explains many common Spring behaviors:

Why `@Transactional` works only on public methods
Why self-invocation breaks transactions
Why proxies replace real beans
How AOP is implemented internally
How Spring extensions integrate into the container

---

# Relation to This Project

This repository demonstrates a simplified version of Spring’s transactional system using:

- Custom Annotation
- Transaction Manager
- Method Interceptor
- BeanPostProcessor
- Dynamic Proxy

The implementation becomes much easier to understand once the **Bean lifecycle described in this document is clear**.
