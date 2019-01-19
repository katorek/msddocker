# Autor
Wojciech Jaronski 127213
## Technologie
* Java 8 
* Maven
* Docker

## Opis

Korzystam z Javy 8
Dane o unikalnych piosenkach wczytuję do HashMapy.
Dane o odsłuchaniach nie trzymam stricte nigdzie, nabieżąco aktualizuję informacje potrzebne do obliczenia podpunktów tj: 
0. ilość odsłuchań pisoenki trzymam w obiekcie UniqueTrack(mam listę UniqueTracków)
0. Unikalne piosenki usera trzymam w specjalnej mapie (HashMap<USERID, HashSet<UniqieTrack>)
0. Osobno trzymam też piosenki Queenów (HashMap<QueenSongId, IlośćOdsłuchań>)
0. Ilość odsłuchań w miesiącu trzymam w liście o 12 elementach(Styczeń 1 element itp)


## Budowa
```
docker build -t pmd_wjaronski .
```

## Uruchomienie
```
docker run --name pmd_wjaronski_container pmd_wjaronski
```