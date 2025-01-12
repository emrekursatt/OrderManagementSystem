
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

5. **Containerizasyon**:
    - **Docker ve Docker Compose**: PostgreSQL, Redis ve RabbitMQ gibi bileşenler container olarak yapılandırıldı.

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
   cd proje-adi
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

