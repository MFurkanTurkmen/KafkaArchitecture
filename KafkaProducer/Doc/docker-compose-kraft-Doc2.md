# Kafka Docker Compose Yapılandırma Dökümantasyonu

## Genel Bakış
Bu döküman, ZooKeeper'a ihtiyaç duymayan KRaft (KIP-500) modunu kullanarak iki düğümlü bir Apache Kafka kümesinin kurulumu için Docker Compose yapılandırmasını detaylı olarak açıklamaktadır.

## Temel Yapı
```yaml
version: '3.8'
services:
  kafka1:
    # kafka1 yapılandırması
  kafka2:
    # kafka2 yapılandırması
networks:
  default:
    name: kraft-network
```

## Servis Yapılandırma Detayları

### Ortak Ayarlar

#### Temel Yapılandırma
```yaml
image: confluentinc/cp-kafka:7.5.0
```
- `image`: Confluent Platform Kafka Docker imajının 7.5.0 versiyonunu belirtir
- Bu, Apache Kafka'nın kurumsal seviye dağıtımıdır

#### Container Kimliği
```yaml
hostname: kafka1
container_name: kafka1
```
- `hostname`: Container için dahili DNS adı
- `container_name`: Docker'daki container'ın adı

#### Port Eşleştirmeleri
```yaml
ports:
  - "9092:9092"    # Dış iletişim
  - "29092:29092"  # İç iletişim
```
- İlk sayı: Ana makinedeki port
- İkinci sayı: Container içindeki port
- İç ve dış iletişim için farklı portlar kullanılır

### Çevre Değişkenleri

#### Düğüm Kimliği
```yaml
KAFKA_BROKER_ID: 1
KAFKA_NODE_ID: 1
```
- Her Kafka broker'ı için benzersiz tanımlayıcılar
- Kümedeki her düğüm için farklı olmalıdır
- Küme koordinasyonu ve yönetimi için kullanılır

#### İşlem Rolleri
```yaml
KAFKA_PROCESS_ROLES: 'broker,controller'
```
- `broker`: Mesaj yayınlama ve aboneliği yönetir
- `controller`: Küme koordinasyonunu yönetir
- KRaft modu, bir düğümün hem broker hem de controller olmasına izin verir

#### Controller Yapılandırması
```yaml
KAFKA_CONTROLLER_QUORUM_VOTERS: '1@kafka1:29093,2@kafka2:29093'
```
- Controller oylamasına katılan tüm düğümleri listeler
- Format: `id@hostname:port`
- Küme koordinasyonu ve lider seçimi için gereklidir

#### Dinleyici Yapılandırması
```yaml
KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: 'CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT'
```
- Farklı bağlantı türleri için güvenlik protokollerini tanımlar
- `CONTROLLER`: İç controller iletişimi için
- `PLAINTEXT`: İç broker iletişimi için
- `PLAINTEXT_HOST`: Dış istemci iletişimi için

```yaml
KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://kafka1:29092,PLAINTEXT_HOST://localhost:9092'
```
- İstemcilere verilecek adresler
- `PLAINTEXT`: İç iletişim adresi
- `PLAINTEXT_HOST`: Dış iletişim adresi

```yaml
KAFKA_LISTENERS: 'PLAINTEXT://kafka1:29092,CONTROLLER://kafka1:29093,PLAINTEXT_HOST://0.0.0.0:9092'
```
- Kafka'nın bağlanacağı gerçek arayüzler ve portlar
- Farklı amaçlar için üç farklı dinleyici

#### Broker'lar Arası İletişim
```yaml
KAFKA_INTER_BROKER_LISTENER_NAME: 'PLAINTEXT'
```
- Broker'lar arası iletişim için hangi dinleyicinin kullanılacağını belirtir
- İç iletişim için PLAINTEXT dinleyicisini kullanır

#### Depolama Yapılandırması
```yaml
KAFKA_LOG_DIRS: '/tmp/kraft-combined-logs'
```
- Kafka'nın log dosyalarını depoladığı dizin
- Hem mesaj verilerini hem de KRaft meta verilerini içerir

#### Çoğaltma Ayarları
```yaml
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
```
- Verilerin kaç kopyasının tutulacağını kontrol eder
- 1 değeri çoğaltma olmadığı anlamına gelir (üretim ortamı için önerilmez)
- Üretim ortamları için hata toleransı açısından daha yüksek değerler kullanılmalıdır

#### Küme Tanımlama
```yaml
CLUSTER_ID: 'MkU3OEVBNTcwNTJENDM2Qk'
```
- Kafka kümesi için benzersiz tanımlayıcı
- Kümedeki tüm düğümlerde aynı olmalıdır

## Ağ Yapılandırması
```yaml
networks:
  default:
    name: kraft-network
```
- Kafka container'ları için özel bir ağ oluşturur
- kafka1 ve kafka2 arasındaki iletişimi sağlar
- Kafka trafiğini diğer Docker container'larından izole eder

## kafka1 ve kafka2 Arasındaki Farklar

İki düğüm arasındaki temel farklar:

1. Port Numaraları:
    - kafka1: 9092, 29092
    - kafka2: 9093, 29094

2. Broker ve Node ID'leri:
    - kafka1: BROKER_ID=1, NODE_ID=1
    - kafka2: BROKER_ID=2, NODE_ID=2

3. Dinleyici Adresleri:
    - Her düğüm kendi host adı ve portlarını kullanır

## Kullanım Önerileri

1. Geliştirme Ortamı İçin:
    - Mevcut yapılandırma yeterlidir
    - Tek kopyalı yapılandırma kabul edilebilir

2. Üretim Ortamı İçin Öneriler:
    - Çoğaltma faktörlerini en az 2 yapın
    - Veri dizinini kalıcı bir volume'a bağlayın
    - Güvenlik protokollerini etkinleştirin
    - Sistem kaynaklarını sınırlandırın
    - Düzenli yedekleme stratejisi belirleyin

## Güvenlik Notları

1. Mevcut Yapılandırma:
    - PLAINTEXT protokolü şifreleme kullanmaz
    - Sadece geliştirme ortamı için uygundur

2. Üretim İçin Öneriler:
    - SSL/TLS yapılandırması ekleyin
    - SASL kimlik doğrulaması kullanın
    - ACL'ler ile erişim kontrolü sağlayın
