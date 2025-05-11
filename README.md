# Optymalizacja płatności za zamówienia

Aplikacja służy do optymalizacji płatności za zamówienia wykorzystując informacje o zamówieniach, dostępnych metodach płatności oraz promocjach przy odpowiednich płatnościach, które są przypisane do danego zamówienia.

## Wyjaśnienie

Każde zamówienie składa się z ID, wartości jaką trzeba zapłacić oraz dostępnych promocji za płatności kartami zapisanych jako ID metody płatności.

Przykład pliku zawierającego zamówienia
```json
[
   {"id": "ORDER1", "value": "100.00", "promotions": ["mZysk"]},
   {"id": "ORDER2", "value": "200.00", "promotions": ["BosBankrut"]},
   {"id": "ORDER3", "value": "150.00", "promotions": ["mZysk", "BosBankrut"]},
   {"id": "ORDER4", "value": "50.00"}
]
```

Każda metoda płatności składa się z ID, oferowanej zniżki procentowej wyrażonej jako liczba całkowita oraz limitu jaki mamy do wykorzystania. Płacić możemy kartami różnych banków lub specjalnymi punkatmi lojalnościowymi oznaczonymi jako
`PUNKTY`

Przykład pliku zawierającego metody płatności
```json
[
 {"id": "PUNKTY", "discount": "15", "limit": "100.00"},
 {"id": "mZysk", "discount": "10", "limit": "180.00"},
 {"id": "BosBankrut", "discount": "5", "limit": "200.00"}
]
```

#### Promocja za użycie punktów nie jest przypisana do zamówień, ponieważ można ją zastosować zawsze.

## Zasady płatności

### Każde zamówienie można opłacić:
- w całości za pomocą jednej tradycyjnej metody płatności (np. jednej karty),
- w całości punktami lojalnościowymi,
- częściowo punktami i częściowo jedną tradycyjną metodą płatności.


1. **Promocje przypisane do zamówienia**  
Do każdego zamówienia przypisany jest wybrany podzbiór możliwych do
zaaplikowania promocji związanych z metodą płatności.

2. **Płatność kartą** 
Jeśli całe zamówienie zostanie opłacone kartą banku, naliczany jest rabat procentowy określony w definicji danej metody płatności.

3. **Częściowa płatność punktami**
Jeśli klient opłaci co najmniej 10% wartości zamówienia (przed rabatem) punktami lojalnościowymi, sklep nalicza dodatkowy rabat w wysokości 10% na całe zamówienie.

4. **Płatność punktami**
Jeśli całe zamówienie zostanie opłacone punktami lojalnościowymi, należy
zastosować rabat zdefiniowany dla metody `PUNKTY`, zamiast rabatu 10% za
częściową płatność punktami.

## Uruchomienie
Aplikacja została napisana przy użyciu `Java 21`
1. Sklonuj repozytorium:
   ```bash
   git clone https://github.com/maropce/Marek_Krosny_Java_Krakow
   ```
2. Przygotuj 2 pliki JSON, zawierające zamówienia oraz metody płatności 
3. Uruchom plik Marek_Krosny_Java_Krakow.jar:
   ```bash
   java -jar Marek_Krosny_Java_Krakow.jar [ścieżka do pliku zamówienia] [ścieżka do pliku metody płatności]
   ```
## Wynik
Aplikacja powinna wyświetlić podsumowanie wydancyh środków dla każdej metody sumarycznie
### Przykład
   ```bash
   PUNKTY 40.0
   mZysk 50.0
   BosBankrut 0.0
   ```
