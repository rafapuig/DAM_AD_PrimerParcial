package org.iesabastos.dam.ad;

import model.academic.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Ejercicios {

    //---- EJERCICIO 1

    /**
     * Devuelve un predicado para comprobar que el número es divisible por un factor
     * @param number número para el que se quiere probar si el factor lo divide
     * @return Un predicado que aplicado a un Long (el factor) comprueba si ese factor es divisor del número
     */
    public static LongPredicate isDivisor(long number) {
        return factor -> number % factor == 0;
    }



    //---- EJERCICIO 2

    /**
     * Devuelve true si el numero proporcionado es primo o false si no
     * Un numero se considera primo si no tiene divisores
     * sin contar el 1 y el propio número
     * @param number numero del que se quiere saber si es primo
     * @return un boolean que indica si es primo o no el número
     */
    public static boolean isPrime(long number) {
        return LongStream.rangeClosed(2, (long) Math.sqrt(number))
                .noneMatch(isDivisor(number));
    }

    public static LongPredicate isPrime() {
        return number -> LongStream.rangeClosed(2, (long) Math.sqrt(number))
                .noneMatch(isDivisor(number));
    }



    //---- EJERCICIO 3

    public static Set<Long> generatePrimes(long upTo) {

        return LongStream.range(1, upTo)
                //.peek(n -> System.out.println("Testing prime: " + n))
                .filter(isPrime())
                //.peek(n -> System.out.println("Adding prime: " + n))
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
    }




    //---- EJERCICIO 4

    /**
     * Devuelve la suma de las horas totales del conjunto de módulos que forman parte del ciclo
     *
     * @param modulos colección de todos módulos de cualquier ciclo
     * @param ciclo   ciclo del que se desea obtener el número de horas total
     * @return las horas totales del ciclo
     */
    public static int getHorasTotales(Collection<Modulo> modulos, Ciclo ciclo) {
        // TODO: implementa el codigo
        return modulos.stream()
                .filter(modulo -> modulo.ciclo().equals(ciclo))
                .mapToInt(Modulo::horas)
                .sum();
    }



    //---- EJERCICIO 5

    /**
     * Devuelve el número de horas que al alumno le quedan pendientes para completar el ciclo
     * Un módulo se considera superado si el valor numérico de su nota es igual o superior a 5
     * Cada módulo tiene un número de horas totales que se acumularan al resultado de haber resultado superado
     * @param calificaciones colección de calificaciones fuente de datos para obtener el resultado
     * @param modulos colección de módulos fuente de información
     * @param alumno alumno para el cual se quiere calcular el número de horas pendientes de superar
     * @param ciclo ciclo para el cual se calculan las horas pendientes del alumno
     * @return un entero con el número de horas pendientes
     */
    public static int getHorasPendientes(
            Collection<Calificacion> calificaciones, Collection<Modulo> modulos, Alumno alumno, Ciclo ciclo) {

        int horasSuperadas = calificaciones.stream()
                .filter(calificacion -> calificacion.alumno().equals(alumno))
                .filter(calificacion -> calificacion.modulo().ciclo().equals(ciclo))
                .filter(calificacion -> calificacion.nota().isPresent())
                .filter(calificacion -> calificacion.nota().get().getNumericValue() >= 5)
                .mapToInt(calificacion -> calificacion.modulo().horas())
                .sum();

        int horasTotalesCiclo = getHorasTotales(modulos, ciclo);

        return horasTotalesCiclo - horasSuperadas;
    }



    //---- EJERCICIO 6

    /**
     * Devuelve un Mapa con clave Alumno y valor el número de horas pendientes para completar el total de horas
     * de que consta el ciclo
     * @param calificaciones colección de calificaciones fuente de información
     * @param modulos        colección de módulos fuente de datos
     * @param ciclo          ciclo del que se quiere saber cuantas horas le faltan al alumno para completarlo
     * @return mapa de alumnos asociado con el número de horas pendientes para terminar el ciclo
     */
    public static Map<Alumno, Integer> getHorasPendientesByCiclo(Collection<Calificacion> calificaciones, Collection<Modulo> modulos, Ciclo ciclo) {

        int horasTotalesCiclo = modulos.stream()
                .filter(calificacion -> calificacion.ciclo().equals(ciclo))
                .mapToInt(Modulo::horas).sum();

        //int horasTotalesCiclo = getHorasTotales(Modulos.MODULOS, ciclo);

        var horasPendientesMap = calificaciones.stream()
                .filter(calificacion -> calificacion.modulo().ciclo().equals(ciclo))
                .filter(calificacion -> calificacion.nota().isPresent())
                .filter(calificacion -> calificacion.nota().get().getNumericValue() >= 5)
                .collect(Collectors.groupingBy(
                        Calificacion::alumno,
                        Collectors.collectingAndThen(
                                Collectors.summingInt(
                                        calificacion -> calificacion.modulo().horas()),
                                horasSuperadas -> horasTotalesCiclo - horasSuperadas)));

        return horasPendientesMap;
    }



    //---- EJERCICIO 7

    /**
     * Proporciona un Supplier que entrega un Stream de elementos Calificacion donde
     * se filtran las calificaciones que pertenecen al alumno y que este ha superado
     * para ello, a partir de la colección de calificaciones se genera un Stream
     * y se le aplican sucesivos operadores intermedios de filtrado para obtener el Stream final
     * @param calificaciones colección de calificaciones para generar el stream
     * @param alumno alumno del que queremos quedarnos con sus calificaciones aplicando el filtro
     * @return un Supplier que proporcionara un Stream donde ya se aplican las operaciones de filtrado
     */
    public static Supplier<Stream<Calificacion>> getFilteredCalificacionesSupplier(Collection<Calificacion> calificaciones, Alumno alumno) {
        return () -> calificaciones.stream()
                .filter(calificacion -> calificacion.alumno().equals(alumno))
                .filter(calificacion -> calificacion.nota().isPresent())
                .filter(calificacion -> calificacion.nota().get().getNumericValue() >= 5);
    }



    //---- EJERCICIO 8

    /**
     * Obtiene el valor decimal de una fracción a partir del numerodor y denominador
     * El valor es envuelto en un OptionalDouble
     * Si el denominador es cero no se puede hacer la division y se devuelve un Optional vacío
     * Si el denominador no es cero entonces se calcula de valor de la fracción
     * y se envuelve en un OptionalDecimal
     * @param numerator numerador de la fracción
     * @param denominator denominador de la fracción
     * @return Un OptionalDouble con el valor decimal de la fracción
     */
    public static OptionalDouble fractionToDecimal(double numerator, long denominator) {
        return denominator != 0 ?
                OptionalDouble.of(numerator/ denominator) :
                OptionalDouble.empty();
    }



    //---- EJERCICIO 9

    /**
     * Collector para calcular medias ponderadas de un Stream<T>
     * A partir de un elemento T puede extraerle el valor y el peso
     * mediante el valueExtractor y el weightExtractor proporcionados respectivamente
     * Se aplicarán dos operaciones de Collector a la vez sobre el mismo Stream<T> mediante un teeing
     * El primer collector suma como double la multiplicación del valor extraído por el peso extraído
     * El segundo collector suma como long los pesos obtenidos mediante el weightExtractor
     * Finalmente ambos resultados son combinados con el "merger"
     * El "merger" tiene que dividir el resultado del primero (numerador)
     * entre el resultado del segundo (denominador)
     * Y envolverlo en un OptionalDouble
     * @param valueExtractor función que se aplica al elemento T para obtener el valor
     * @param weightExtractor función que se aplica al elemento T para obtener el peso
     * @return Un colector que calcula la media ponderada de los elementos del Stream
     * @param <T> el tipo de elementos del Stream que se va a recolectar por el collector
     */
    public static <T> Collector<T, ?, OptionalDouble> averagingWeighted(
            ToDoubleFunction<T> valueExtractor, ToLongFunction<T> weightExtractor) {
        return Collectors.teeing(
                Collectors.summingDouble(
                        elem -> valueExtractor.applyAsDouble(elem) * weightExtractor.applyAsLong(elem)),
                Collectors.summingLong(weightExtractor),
                Ejercicios::fractionToDecimal);
    }


    //-------- EJERCICIO 10

    /**
     * Obtiene la nota media del expediente de un alumno a partir de las calificaciones
     * Para calcular la nota media solamente se tienen en cuenta las asignaturas superadas
     * Cada asignatura tiene un número de horas que da un peso a la nota del módulo en el cálculo de la media
     * El cálculo de una media ponderada se obtiene
     * sumando cada nota multiplicada por el número de horas del módulo (numerador)
     * Sumando el número de horas superadas (denominador)
     * Dividiendo el numerador entre el denominador (si es distinto de cero)
     * Si el denominador es cero, lo que querrá decir que el alumno no ha superado ninguna asignatura
     * no se puede hacer la division, entonces devuelve un OptionalDouble vacío
     * Si es posible obtener el valor entonces se envuelve en un OptionalDouble y se devuelve   *
     *
     * @param calificaciones colecciones de calificaciones fuente de datos para obtener el resultado
     * @param alumno         alumno del que se quiere obtener la nota media
     * @param ciclo          ciclo del cual se quieren obtener las notas del alumno
     * @return Un OptionalDouble que contiene el valor de la nota medio o vacío si no hay ningún módulo superado
     */

    public static OptionalDouble getNotaMediaExpediente(Collection<Calificacion> calificaciones, Alumno alumno, Ciclo ciclo) {

        return calificaciones.stream()
                .filter(calificacion -> calificacion.alumno().equals(alumno))
                .filter(calificacion -> calificacion.modulo().ciclo().equals(ciclo))
                .filter(calificacion -> calificacion.nota().isPresent())
                .filter(calificacion -> calificacion.nota().get().getNumericValue() >= 5)
                //.peek(System.out::println)
                .collect(averagingWeighted(
                        calificacion -> calificacion.nota().orElseThrow().getNumericValue(),
                        calificacion -> calificacion.modulo().horas()));
    }


    //---- EJERCICIO 11

    /**
     * Obtiene a partir de la información que proporciona la colección de calificaciones
     * un mapa de nota media de todos los alumnos que tengan calificaciones en el ciclo
     * proporcionado como parámetro
     * Las entradas del mapa se ordenan por el valor de la nota media en orden descendente
     * Al final se obtiene un mapa cuyas claves son los alumnos y con un orden que se establece
     * por la nota media del alumno de mayor a menor
     * @param calificaciones colección de calificaciones fuente de información para obtener el resultado
     * @param ciclo ciclo del cual se quiere obtener el mapa de alumnos y su nota media
     * @return mapa de claves alumno cuyo valor asociado es la nota media de todas las asignaturas aprobadas
     */

    public static Map<Alumno, OptionalDouble> getNotaMediaExpediente(Collection<Calificacion> calificaciones, Ciclo ciclo) {

        return calificaciones.stream()
                .filter(calificacion -> calificacion.modulo().ciclo().equals(ciclo))
                .filter(calificacion -> calificacion.nota().isPresent())
                .filter(calificacion -> calificacion.nota().get().getNumericValue() >= 5)
                .filter(calificacion -> !calificacion.modulo().abreviatura().equals("FCT"))
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(
                                Calificacion::alumno,
                                averagingWeighted(
                                        calificacion -> calificacion.nota().orElseThrow().getNumericValue(),
                                        calificacion -> calificacion.modulo().horas())),
                        result -> result.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue(
                                        Comparator.comparing(opt -> opt.orElse(0),
                                                Comparator.reverseOrder())))
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (e1, e2) -> e1,
                                        LinkedHashMap::new))));
    }


}
