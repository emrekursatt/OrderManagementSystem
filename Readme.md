

# Order Management System

## General Information

This project aims to develop an **Order Management System**. The application, built using Spring Boot, includes core functionalities such as customer management, order processing, and automated discount application. The primary objective is to create a user-friendly and scalable order management system utilizing a modern microservice architecture.

### Key Features
1. **Customer Management**:
    - Creating new customers.
    - Managing customer tiers (Regular, Gold, Platinum).
    - Automatically upgrading customer tiers based on the number of orders.

2. **Order Processing**:
    - Allowing customers to place orders.
    - Finalizing orders with payment status marked as completed by default.

3. **Discount System**:
    - Applying a 10% discount for Gold customers and a 20% discount for Platinum customers.
    - Tracking and storing discount information.

4. **Messaging System**:
    - Asynchronous tier check for customers using RabbitMQ.

---

## Technologies and Architecture

### Technologies and Libraries

1. **Spring Framework**:
    - **Spring Boot**: Provides the core infrastructure for the application.
    - **Spring Data JPA**: Used for database operations.
    - **Spring Security**: Authentication and authorization.
    - **Spring AMQP (RabbitMQ)**: Messaging between services.
    - **Spring Scheduler**: Managing scheduled tasks.

2. **Dependencies**:
    - **Lombok**: Reduces boilerplate code and speeds up development.
    - **Swagger**: For REST API documentation.

3. **Database and Cache**:
    - **PostgreSQL**: Handles all database operations.
    - **Redis**: Used for caching and token management.

4. **Messaging**:
    - **RabbitMQ**: Supports asynchronous processes and messaging.

5. **Testing**:
    - **JUnit** and **Mockito**: For unit and integration testing.

6. **Containerization**:
    - **Docker and Docker Compose**: Configures components like PostgreSQL, Redis, and RabbitMQ as containers.

---

## Additional Features

### Test Coverage

The project includes comprehensive unit and integration tests:
- **Order Service Tests**:
    - `OrderProductServiceTest`
    - `OrdersServiceTest`
    - `ProductsServiceTest`

- **Customer Service Tests**:
    - `AuthenticationServiceTest`
    - `CustomerServiceTest`
    - `PasswordServiceTest`
    - `RegistrationServiceTest`

These tests ensure functional correctness using JUnit and Mockito.

### Scheduled Tasks

- **@EnableScheduling and Spring Scheduler**:
    - The project includes scheduled tasks such as the `TierNotifications` class.
    - The `notifyCustomers` method in `TierNotifications` runs every 10 seconds to check customer tiers and send notifications:
      ```java
      @Slf4j
      @Component
      @RequiredArgsConstructor
      public class TierNotifications {
          private final CustomerService customerService;
  
          private static final String CRON_EVERY_10_SECONDS = "0/10 * * * * *";
  
          @Scheduled(cron = CRON_EVERY_10_SECONDS)
          public void notifyCustomers() {
              log.info("Notifying customers");
              customerService.notifyCustomers();
          }
      }
      ```
    - This task uses Spring's scheduling mechanism with `@Scheduled`.

---

## Database Design

The project features a database design tailored for two main services: `Customer Service` and `Order Service`. Both services are built using PostgreSQL. Below are the tables and relationships for each service:

### Customer Service Database

1. **Tables**:
    - `customer`: Stores customer information (username, password, email, etc.).
    - `tiers`: Manages customer tiers (Regular, Gold, Platinum).
    - `tiers_history`: Tracks customer tier history.

2. **Functionality**:
    - Handles customer registration and login.
    - Dynamically updates customer tiers.
    - Manages discount rates based on tiers.

### Order Service Database

1. **Tables**:
    - `orders`: Stores order information.
    - `order_products`: Tracks products added to orders.
    - `payments`: Records payment methods (CREDIT_CARD, PAYPAL, CASH, BANK_TRANSFER).
    - `products`: Stores product details (name, price, stock, etc.).

2. **Functionality**:
    - Creates and tracks orders.
    - Manages products (add, list).
    - Tracks payment transactions.

---

## Installation and Execution Instructions

### 1. Requirements

- **Java 21** (JDK 21)
- **Gradle 9**
- **Docker and Docker Compose**
- An IDE (Recommended: IntelliJ IDEA)

### 2. Clone and Setup the Project

1. Clone the project from GitHub:
   ```bash
   git clone https://github.com/emrekursatt/OrderManagementSystem.git
   cd OrderManagementSystem
   ```

2. Start PostgreSQL, Redis, and RabbitMQ using Docker Compose:
   ```bash
   docker-compose up -d
   ```

### 3. Running the Application

1. Open the project in an IDE (Recommended: IntelliJ IDEA).
2. Ensure Gradle configurations are properly loaded.
3. Run the following main classes in order:
    - `EurekaServerApplication`
    - `APIGatewayApplication`
    - `CustomerManagementSystemApplication`
    - `OrderProcessingSystemApplication`

### 4. Monitoring Services and API Documentation

- [http://localhost/wallboard](http://localhost/wallboard): Monitor service statuses.
- [http://localhost:8761](http://localhost:8761): Monitor services via Eureka.
- [Customer Service Swagger](http://localhost/customer/swagger-ui/index.html#/)
- [Order Service Swagger](http://localhost/order/swagger-ui/index.html#/)

### 5. Application Usage

#### User Registration and Login

- Register a user:
   ```bash
   curl -X 'POST' 'http://localhost/customer/api/v1/customer/customer-register'         -H 'accept: */*'         -H 'Content-Type: application/json'         -d '{
            "userName": "testUser",
            "password": "newPassword12345",
            "email": "testUser@gmail.com",
            "name": "Test Customer Name"
        }'
   ```

- Login:
   ```bash
   curl -X 'POST' 'http://localhost/customer/login'         -H 'accept: */*'         -H 'Content-Type: application/json'         -d '{
            "userName": "testUser",
            "password": "newPassword12345"
        }'
   ```

#### Product Management

- Add a new product:
   ```bash
   curl -X 'POST' 'http://localhost/order/api/v1/products/add-product'         -H 'Authorization: Bearer TOKEN'         -H 'Content-Type: application/json'         -d '{
            "productName": "NewTestProduct",
            "price": 10.5,
            "stocks": 500
        }'
   ```

- List all products:
   ```bash
   curl -X 'GET' 'http://localhost/order/api/v1/products/all-product'         -H 'Authorization: Bearer TOKEN'
   ```

#### Order Management

- Create an order:
- paymentMethod = CREDIT_CARD, PAYPAL, CASH, BANK_TRANSFER
- After an order is created, a message is triggered via RabbitMQ. This message is processed asynchronously by the Customer Service.Customer Service checks the total number of orders placed by the customer. If the 
  customer's total order count exceeds a specific threshold (e.g., 10 or 20 orders), their tier information is updated accordingly. This process ensures that customer tier progression is dynamically and efficiently 
  managed in real time.
   ```bash
   curl -X 'POST' 'http://localhost/order/api/v1/orders/create-order?paymentMethod=CREDIT_CARD'         -H 'Authorization: Bearer TOKEN'
   ```

- View orders:
    - All orders:
      ```bash
      curl -X 'GET' 'http://localhost/order/api/v1/orders/all-order-products'            -H 'Authorization: Bearer TOKEN'
      ```

    - Specific customer orders:
      ```bash
      curl -X 'GET' 'http://localhost/order/api/v1/orders/all-order-products/{customerEmail}?customerEmail=testUser%40gmail.com'            -H 'Authorization: Bearer TOKEN'
      ```

---

## Important Note

When running Docker Compose, the `init.sql` file initializes necessary tables and data in the PostgreSQL database. This automates database configuration and initial setup.

**Key Functions of the `init.sql` file**:
- Creates tables required for Customer Service and Order Service.
- Defines tier levels (Regular, Gold, Platinum) and discount rates.






# Order Management System

## Genel Bilgi

Bu proje, bir **Sipariş Yönetim Sistemi** geliştirmeyi amaçlamaktadır. Spring Boot tabanlı olarak oluşturulan uygulama, müşteri yönetimi, sipariş işleme ve otomatik indirim uygulama gibi çekirdek işlevleri içermektedir. Projenin temel amacı, modern bir mikroservis mimarisi kullanarak kullanıcı dostu ve ölçeklenebilir bir sipariş yönetim sistemi geliştirmektir.

### Projenin Temel Özellikleri
1. **Müşteri Yönetimi**:
    - Müşteri oluşturma.
    - Müşteri seviyelerinin (Regular, Gold, Platinum) yönetimi.
    - Sipariş sayısına bağlı müşteri seviye yükselmesi.

2. **Sipariş İşleme**:
    - Müşterilerin sipariş oluşturması.
    - Siparişlerin nihai hale getirilmesi ve ödeme durumunun varsayılan olarak tamamlanmış kabul edilmesi.

3. **İndirim Sistemi**:
    - Gold müşterilere %10, Platinum müşterilere %20 otomatik indirim uygulanması.
    - İndirim bilgisi takibi ve saklanması.

4. **Mesajlaşma Sistemi**:
    - RabbitMQ ile müşteri seviyesinin asenkron kontrolü.

---

## Kullanılan Teknolojiler ve Mimariler

### Teknolojiler ve Kütüphaneler

1. **Spring Framework**:
    - **Spring Boot**: Uygulamanın temel altyapısını sağlar.
    - **Spring Data JPA**: Veri tabanı işlemleri için kullanıldı.
    - **Spring Security**: Kimlik doğrulama ve yetkilendirme.
    - **Spring AMQP (RabbitMQ)**: Servisler arası mesajlaşma.
    - **Spring Scheduler**: Zamanlanmış görevlerin yönetimi.

2. **Bağımlılıklar**:
    - **Lombok**: Kod tekrarını azaltmak ve geliştirme sürecini hızlandırmak için.
    - **Swagger**: REST API dokümantasyonu için.

3. **Veri Tabanı ve Cache**:
    - **PostgreSQL**: Uygulamanın tüm veri tabanı işlemleri için kullanıldı.
    - **Redis**: Cache işlemleri ve token yönetimi.

4. **Mesajlaşma**:
    - **RabbitMQ**: Asenkron işlemler ve mesajlaşma altyapısı.

5. **Test**:
    - **JUNIT** ve  **Mockito**: Birim ve entegrasyon testleri için.

6. **Containerizasyon**:
    - **Docker ve Docker Compose**: PostgreSQL, Redis ve RabbitMQ gibi bileşenler container olarak yapılandırıldı.

---

## Ek Özellikler

### Test Kapsamı

Proje, aşağıdaki testler ile birim ve entegrasyon testlerini kapsamaktadır:
- **Order Service Testleri**:
    - `OrderProductServiceTest`
    - `OrdersServiceTest`
    - `ProductsServiceTest`

- **Customer Service Testleri**:
    - `AuthenticationServiceTest`
    - `CustomerServiceTest`
    - `PasswordServiceTest`
    - `RegistrationServiceTest`

Bu testler, işlevlerin doğru çalıştığından emin olmak için JUnit ve Mockito kullanılarak yazılmıştır.

### Zamanlanmış Görevler

- **@EnableScheduling ve Spring Scheduler**:
    - Projede, belirli aralıklarla çalışan zamanlanmış görev olarak TierNotifications sınıfı bulunmaktadır.r.
    - `TierNotifications` sınıfında yer alan `notifyCustomers` metodu, müşteri seviyelerinin düzenli kontrol edilmesi ve gerekli bilgilendirmelerin yapılması için her 10 saniyede bir çalışmaktadır:
      ```java
      @Slf4j
      @Component
      @RequiredArgsConstructor
      public class TierNotifications {
          private final CustomerService customerService;
  
          private static final String CRON_EVERY_10_SECONDS = "0/10 * * * * *";
  
          @Scheduled(cron = CRON_EVERY_10_SECONDS)
          public void notifyCustomers() {
              log.info("Notifying customers");
              customerService.notifyCustomers();
          }
      }
      ```
    - Bu işlem, Spring’in zamanlama mekanizması olan `@Scheduled` ile gerçekleştirilmiştir.

---

## Veritabanı Tasarımı

Proje, iki ana hizmet olan `Customer Service` ve `Order Service` için ayrılmış bir veritabanı tasarımı içermektedir. Bu hizmetler, PostgreSQL kullanılarak yapılandırılmıştır. Her hizmetin veritabanı tabloları ve ilişkileri aşağıda açıklanmıştır:

### Customer Service Veritabanı

1. **Tablolar**:
    - `customer`: Müşteri bilgilerini saklar. (Kullanıcı adı, şifre, e-posta, vb.)
    - `tiers`: Müşteri seviyelerini (Regular, Gold, Platinum) yönetir.
    - `tiers_history`: Müşterilerin seviye geçmişlerini takip eder.

2. **İşlevsellik**:
    - Müşteri kayıt ve giriş işlemleri.
    - Müşteri seviyesinin (tier) dinamik olarak güncellenmesi.
    - İndirim oranlarının seviyeye bağlı olarak yönetilmesi.

### Order Service Veritabanı

1. **Tablolar**:
    - `orders`: Sipariş bilgilerini saklar.
    - `order_products`: Siparişlere eklenen ürünleri takip eder.
    - `payments`: Siparişlerin ödeme yöntemlerini kaydeder. (CREDIT_CARD, PAYPAL, CASH, BANK_TRANSFER)
    - `products`: Ürün bilgilerini saklar. (Ürün adı, fiyat, stok, vb.)

2. **İşlevsellik**:
    - Sipariş oluşturma ve takip.
    - Ürün yönetimi (ekleme, listeleme).
    - Ödeme işlemlerinin takibi.

---

## Kurulum ve Çalıştırma Talimatları

### 1. Gereksinimler

- **Java 21** (JDK 21)
- **Gradle 9**
- **Docker ve Docker Compose**
- Bir IDE (Önerilen: IntelliJ IDEA)

### 2. Projeyi İndirme ve Kurulum

1. GitHub'dan projeyi klonlayın:
   ```bash
   git clone https://github.com/emrekursatt/OrderManagementSystem.git
   cd OrderManagementSystem
   ```

2. Docker Compose kullanarak PostgreSQL, Redis ve RabbitMQ'yu başlatın:
   ```bash
   docker-compose up -d
   ```

### 3. Uygulamayı Çalıştırma

1. Projeyi IDE'de açın (Önerilen: IntelliJ IDEA).
2. Gradle yapılandırmasının doğru bir şekilde yüklendiğinden emin olun.
3. Aşağıdaki ana sınıfları sırasıyla çalıştırın:
    - `EurekaServerApplication`
    - `APIGatewayApplication`
    - `CustomerManagementSystemApplication`
    - `OrderProcessingSystemApplication`

### 4. Servislerin İzlenmesi ve API Dökümantasyonu

- [http://localhost/wallboard](http://localhost/wallboard): Servislerin durumu izlenebilir.
- [http://localhost:8761](http://localhost:8761): Eureka üzerinden servisler takip edilebilir.
- [Customer Service Swagger](http://localhost/customer/swagger-ui/index.html#/)
- [Order Service Swagger](http://localhost/order/swagger-ui/index.html#/)

### 5. Uygulama Kullanımı

#### Kullanıcı Kayıt ve Giriş İşlemleri

- Kullanıcı kaydı:
   ```bash
   curl -X 'POST'      'http://localhost/customer/api/v1/customer/customer-register'      -H 'accept: */*'      -H 'Content-Type: application/json'      -d '{
       "userName": "testUser",
       "password": "newPassword12345",
       "email": "testUser@gmail.com",
       "name": "Test Customer Name"
     }'
   ```

- Giriş işlemi:
   ```bash
   curl -X 'POST'      'http://localhost/customer/login'      -H 'accept: */*'      -H 'Content-Type: application/json'      -d '{
       "userName": "testUser",
       "password": "newPassword12345"
     }'
   ```

#### Ürün Yönetimi

- Yeni ürün eklemek:
   ```bash
   curl -X 'POST'      'http://localhost/order/api/v1/products/add-product'      -H 'Authorization: Bearer TOKEN'      -H 'Content-Type: application/json'      -d '{
       "productName": "NewTestUrun",
       "price": 10.5,
       "stocks": 500
     }'
   ```

- Tüm ürünleri listelemek:
   ```bash
   curl -X 'GET'      'http://localhost/order/api/v1/products/all-product'      -H 'Authorization: Bearer TOKEN'
   ```

#### Sipariş Yönetimi

- Sipariş oluşturmak:
- paymentMethod = CREDIT_CARD, PAYPAL, CASH, BANK_TRANSFER
- Sipariş oluşturulduktan sonra, RabbitMQ üzerinden bir mesaj tetiklenir. Bu mesaj, asenkron bir işlem olarak Customer Service'e iletilir.Customer Service, gelen mesajı işleyerek ilgili müşterinin toplam sipariş 
  sayısını kontrol eder. Eğer müşterinin sipariş sayısı belirli bir eşiği (örneğin, 10 veya 20 sipariş) aşarsa, müşterinin tier bilgisi güncellenir. Bu işlem sayesinde müşterilerin seviye ilerlemesi dinamik ve gerçek 
  zamanlı olarak yönetilir.
   ```bash
   curl -X 'POST'      'http://localhost/order/api/v1/orders/create-order?paymentMethod=CREDIT_CARD'      -H 'Authorization: Bearer TOKEN'
   ```

- Siparişleri görüntülemek:
    - Tüm siparişler:
      ```bash
      curl -X 'GET'        'http://localhost/order/api/v1/orders/all-order-products'        -H 'Authorization: Bearer TOKEN'
      ```

    - Belirli bir müşterinin siparişleri:
      ```bash
      curl -X 'GET'        'http://localhost/order/api/v1/orders/all-order-products/{customerEmail}?customerEmail=testUser%40gmail.com'        -H 'Authorization: Bearer TOKEN'
      ```

---

## Önemli Not

Docker Compose çalıştırıldığında, `init.sql` dosyası PostgreSQL veritabanında gerekli tabloları ve başlangıç verilerini oluşturacaktır. Bu işlem, veritabanı yapılandırmasını ve ilk yapılandırmaları otomatikleştirir.

**init.sql dosyasının temel işlevleri**:
- Customer Service ve Order Service için gerekli tabloları oluşturur.
- Tier seviyelerini (Regular, Gold, Platinum) ve indirim oranlarını tanımlar.
