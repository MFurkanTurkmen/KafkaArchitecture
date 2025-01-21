# Kafka Kraft Mode with Docker Compose
Bu dosya, Kafka'nın Docker container'larıyla çalışması için bir docker-compose.yml dosyasının örneğidir. Docker Compose, birden fazla container'ı tanımlamanıza ve aynı anda çalıştırmanıza olanak sağlar. Bu dosya, Kafka'nın iki farklı node'u (kafka1 ve kafka2) üzerinde çalışacak şekilde yapılandırılmıştır. Aşağıda her satırın ne işe yaradığını detaylı şekilde açıklayacağız.  
`version: '3.8'`  
Bu satır, Docker Compose dosyasının sürümünü belirtir. Burada 3.8 sürümü kullanılmıştır. Bu sürüm, bir dizi özellik ve uyumluluk sağlamak için kullanılır.
## Services
### Kafka1
```
kafka1:
  image: confluentinc/cp-kafka:7.5.0
  hostname: kafka1
  container_name: kafka1

```
- image: Kafka container'ının hangi Docker imajından oluşturulacağını belirtir. Burada confluentinc/cp-kafka:7.5.0 imajı kullanılmıştır, bu da Confluent tarafından sağlanan Kafka'nın 7.5.0 sürümünü belirtir.
- hostname: Kafka container'ının host adı. Bu, Kafka'nın diğer broker'larla haberleşmesi için kullanılır.
- container_name: Container'a verilecek isim. Bu, container'ı Docker komutlarıyla tanımlarken kullanılır.

```
ports:
  - "9092:9092"
  - "29092:29092"
```
- ports: Bu satır, container'daki portları, host makinedeki portlarla eşler.
- Burada 9092 portu Kafka'nın client'larla iletişim kuracağı port, 29092 ise Kafka'nın broker'lar arasında iletişim kuracağı porttur.

```
environment:
  KAFKA_BROKER_ID: 1
  KAFKA_NODE_ID: 1
  KAFKA_PROCESS_ROLES: 'broker,controller'
  KAFKA_CONTROLLER_QUORUM_VOTERS: '1@kafka1:29093,2@kafka2:29093'
```
- environment: Bu bölüm, Kafka container'ına gerekli ortam değişkenlerini sağlar.
- KAFKA_BROKER_ID: Kafka broker'ının ID'si. Burada 1 olarak belirtilmiştir. Her broker'ın benzersiz bir ID'ye sahip olması gerekir.
- KAFKA_NODE_ID: Kafka node ID'si. Bu değer genellikle broker ID ile aynı olabilir, ancak burada 1 olarak belirtilmiştir.
- KAFKA_PROCESS_ROLES: Kafka'nın bu node'da hangi rolleri üstleneceğini belirtir. Burada her iki node da hem broker hem de controller rolüne sahiptir.
- KAFKA_CONTROLLER_QUORUM_VOTERS: Kafka controller'larının hangi node'lar üzerinde bulunduğunu belirtir. Burada kafka1 ve kafka2 node'ları controller olarak yapılandırılmıştır.

```
  KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: 'CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT'
  KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://kafka1:29092,PLAINTEXT_HOST://localhost:9092'
  KAFKA_LISTENERS: 'PLAINTEXT://kafka1:29092,CONTROLLER://kafka1:29093,PLAINTEXT_HOST://0.0.0.0:9092'
  KAFKA_INTER_BROKER_LISTENER_NAME: 'PLAINTEXT'
  KAFKA_CONTROLLER_LISTENER_NAMES: 'CONTROLLER'

```
- KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: Kafka'nın dinleyeceği güvenlik protokollerini belirtir. Burada PLAINTEXT kullanılarak şifrelenmemiş iletişim sağlanır.
- KAFKA_ADVERTISED_LISTENERS: Bu, diğer Kafka broker'larının, bu broker'ı hangi adresle bulacağını belirler. PLAINTEXT://kafka1:29092 dış dünyaya (kendi içindeki host ve port) Kafka'nın duyurulacak adresidir.
- KAFKA_LISTENERS: Kafka'nın dinleyeceği adresler. PLAINTEXT://kafka1:29092 adresi broker'lar arası iletişim için, CONTROLLER://kafka1:29093 controller işlemleri için kullanılır.
- KAFKA_INTER_BROKER_LISTENER_NAME: Kafka'nın inter-broker iletişim için hangi dinleyiciyi kullanacağını belirtir. Burada PLAINTEXT kullanılmıştır.
- KAFKA_CONTROLLER_LISTENER_NAMES: Kafka controller'ının dinleyeceği dinleyici isimlerini belirtir. Burada CONTROLLER olarak belirlenmiştir.

```
  KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
  KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
  KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
  KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
  KAFKA_LOG_DIRS: '/tmp/kraft-combined-logs'
  CLUSTER_ID: 'MkU3OEVBNTcwNTJENDM2Qk'

```
- KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: Kafka'nın offset'leri tutmak için kullandığı topic'in replikasyon faktörü. Burada 1 olarak ayarlanmış, bu da verinin yalnızca bir kopyasının tutulacağı anlamına gelir.
- KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: Kafka'nın grup yeniden dengeleme işlemine başlamadan önce bekleyeceği süreyi belirtir. 0 olarak ayarlandığında, hemen başlanır.
- KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: Kafka'nın işlem durumunu takip ettiği logların minimum ISR (in-sync replicas) sayısını belirtir.
- KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: Kafka'nın işlem durumunu loglayan topic'in replikasyon faktörünü belirtir. Burada 1 olarak ayarlanmıştır.
- KAFKA_LOG_DIRS: Kafka'nın log dosyalarını tutacağı dizini belirtir. Burada /tmp/kraft-combined-logs olarak belirlenmiştir.
- CLUSTER_ID: Kafka cluster'ı için benzersiz bir ID sağlar.

### kafka2
kafka2 servisi, kafka1 servisi ile çok benzerdir ve yalnızca bazı değerlerde değişiklikler vardır:
```
kafka2:
  image: confluentinc/cp-kafka:7.5.0
  hostname: kafka2
  container_name: kafka2
  ports:
    - "9093:9093"
    - "29094:29094"
  environment:
    KAFKA_BROKER_ID: 2
    KAFKA_NODE_ID: 2
    KAFKA_PROCESS_ROLES: 'broker,controller'
    KAFKA_CONTROLLER_QUORUM_VOTERS: '1@kafka1:29093,2@kafka2:29093'
    ...
```
- Burada kafka2 için farklı bir KAFKA_BROKER_ID (2), KAFKA_NODE_ID (2), ve portlar (9093, 29094) kullanılmıştır.
- Ayrıca KAFKA_CONTROLLER_QUORUM_VOTERS değerinde de kafka2'nin kendi broker'ı da belirtilmiştir.

### Network
```
networks:
  default:
    name: kraft-network

```
- networks: Kafka container'ları arasında bir ağ yapılandırması sağlar. Burada kraft-network adında bir ağ oluşturulmuştur. Bu ağ üzerinden kafka1 ve kafka2 birbirleriyle iletişim kuracaktır.

## *Dosyamızda iki tane Kafka var (kafka1 ve kafka2). Bunlar birbirinin yedeği gibi çalışır. Biri çökerse diğeri devam eder.*
## *Bu yapılandırma, iki Kafka broker'ı (kafka1 ve kafka2) ve her birinin controller rolünü üstlendiği bir ortamı tanımlar. Ayrıca her bir broker için uygun listener'lar, port eşlemeleri, replikasyon faktörleri ve log dizinleri gibi Kafka'nın çalışma ayarlarını içerir.*

## Anlatım
```
version: '3.8'  # Bu, kullanacağımız Docker versiyonu
services:  # Burada çalıştıracağımız programları tanımlıyoruz
  kafka1:  # Birinci Kafka sunucumuz
    image: confluentinc/cp-kafka:7.5.0  # Kafka programımızın versiyonu
    hostname: kafka1  # Programın ismi
    container_name: kafka1  # Docker'daki ismi
    ports:  # Dış dünyayla bağlantı noktaları
      - "9092:9092"  
      - "29092:29092"
    environment:  # Programın çalışması için gereken ayarlar
      KAFKA_BROKER_ID: 1  # Bu Kafka'nın numarası (1. Kafka)
      KAFKA_NODE_ID: 1    # Yine aynı numarayı başka bir yerde kullanmak için
      
      KAFKA_PROCESS_ROLES: 'broker,controller'  # Bu Kafka'nın görevleri
      
      KAFKA_CONTROLLER_QUORUM_VOTERS: '1@kafka1:29093,2@kafka2:29093'  # İki Kafka'nın birbirleriyle konuşma adresleri  
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: 'CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT'  # Güvenlik ayarları
      
      KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://kafka1:29092,PLAINTEXT_HOST://localhost:9092'  # Kafka'nın adresleri
      
      KAFKA_LISTENERS: 'PLAINTEXT://kafka1:29092,CONTROLLER://kafka1:29093,PLAINTEXT_HOST://0.0.0.0:9092'  # Kafka'nın dinlediği adresler
      KAFKA_LOG_DIRS: '/tmp/kraft-combined-logs'  # Mesajların kaydedileceği yer
      CLUSTER_ID: 'MkU3OEVBNTcwNTJENDM2Qk'  # Sistemin özel kimlik numarası
    
```
```
networks:
  default:
    name: kraft-network  # Bu iki Kafka'nın birbirleriyle konuşabilmesi için ağın adı
```

## Basit anlatım:
```
services:  # Burası bizim ana mutfağımız gibi. İçinde iki tane aşçımız (kafka1 ve kafka2) çalışacak
kafka1:  # Bu bizim ilk aşçımız
    image: confluentinc/cp-kafka:7.5.0  # Bu aşçının kullanacağı üniforma (Kafka yazılımının versiyonu)
    hostname: kafka1  # Aşçının ismi - diğerleri onu bu isimle çağıracak
    container_name: kafka1  # Mutfakta kullanacağı yaka kartı
    ports:  # Bunlar mutfağın kapıları - dışarıdan siparişlerin geleceği yerler
      - "9092:9092"  # Dış dünyadan gelen siparişler için kapı
      - "29092:29092"  # Mutfak içi iletişim için kapı
      environment:  # Bunlar aşçının çalışma kuralları
      KAFKA_BROKER_ID: 1  # Aşçının kimlik numarası
      KAFKA_NODE_ID: 1  # Aşçının başka bir kimlik numarası
      
      KAFKA_PROCESS_ROLES: 'broker,controller'  # Aşçının görevleri: hem yemek yapıyor hem de diğer aşçıları yönetiyor
      
      KAFKA_CONTROLLER_QUORUM_VOTERS: '1@kafka1:29093,2@kafka2:29093'  # İki aşçının birlikte karar vermesi gereken durumlar için iletişim bilgileri
      
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: 'CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT'  # Nasıl iletişim kuracağının kuralları - sanki farklı dillerde konuşmak gibi
      
      KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://kafka1:29092,PLAINTEXT_HOST://localhost:9092'  # Aşçının "Ben buradayım!" diye bağırdığı adresler
      
      KAFKA_LISTENERS: 'PLAINTEXT://kafka1:29092,CONTROLLER://kafka1:29093,PLAINTEXT_HOST://0.0.0.0:9092'  # Aşçının dinlediği yerler - sanki üç kulağı var gibi
      
      KAFKA_INTER_BROKER_LISTENER_NAME: 'PLAINTEXT'  # Aşçıların kendi aralarında nasıl konuşacağı
      
      KAFKA_CONTROLLER_LISTENER_NAMES: 'CONTROLLER'  # Yönetici olarak hangi kulağını kullanacağı
      
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1  # Her siparişin kaç kopyası tutulacak
      
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0  # Yeni bir garson geldiğinde hemen çalışmaya başlasın
      
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1  # Sipariş defterini tutmak için minimum kaç kişi gerekiyor
      
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1  # Sipariş defterinin kaç kopyası olacak
      
      KAFKA_LOG_DIRS: '/tmp/kraft-combined-logs'  # Günlük defterinin tutulacağı yer
      
      CLUSTER_ID: 'MkU3OEVBNTcwNTJENDM2Qk'  # Mutfağın özel kimlik numarası
```
**Kafka2 için olan ayarlar da aynı, sadece numara ve adresler farklı! İkinci aşçımız için:**
- Farklı kapılar kullanıyor (9093 ve 29094)
- Farklı kimlik numaraları var (BROKER_ID: 2, NODE_ID: 2)
- Farklı adresler kullanıyor ama aynı şekilde çalışıyor

```
networks:
  default:
    name: kraft-network  # Bu bizim mutfağımızın bulunduğu binanın adı
```



