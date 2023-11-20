import model.academic.*;
import org.iesabastos.dam.ad.Ejercicios;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.iesabastos.dam.ad.Ejercicios.*;


public class EjerciciosTest {


    //---- TEST del EJERCICIO 1

    @Test
    void testIsDivisorPredicate() {
        assertTrue(Ejercicios.isDivisor(60).test(12));
        assertTrue(Ejercicios.isDivisor(60).test(5));
        assertTrue(Ejercicios.isDivisor(60).test(10));
        assertTrue(Ejercicios.isDivisor(60).test(3));
        assertTrue(Ejercicios.isDivisor(60).test(5));
        assertTrue(Ejercicios.isDivisor(60).test(2));
        assertTrue(Ejercicios.isDivisor(60).test(15));
        assertTrue(Ejercicios.isDivisor(60).test(30));
        assertFalse(Ejercicios.isDivisor(17).test(2));
        assertFalse(Ejercicios.isDivisor(18).test(5));
    }



    //---- TEST del EJERCICIO 2

    @ParameterizedTest
    @CsvSource(value = {"31,true", "17,true", "25,false","16038329,true"})
    void testPrimo(long number, boolean expected) {
        if(expected) {
            assertTrue(Ejercicios.isPrime(number), "El numero " + number + " es primo");
        } else {
            assertFalse(Ejercicios.isPrime(number), "El numero " + number + " NO es primo");
        }
    }



    //--- TEST del EJERCICIO 3

    @Test
    void testGeneratePrimes() {
        Set<Long> primes =  Ejercicios.generatePrimes(2_000_000L);
        //System.out.println(primes);
        assertThat(primes.size(), is(148_934));
    }



    //---- TEST del EJERCICIO 4

    static Stream<Arguments> providerCiclo_HorasTotalesExpected() {
        return Stream.of(
                Arguments.of(Ciclos.DAM, 2000),
                Arguments.of(Ciclos.DAW, 2000)
        );
    }

    @ParameterizedTest
    @MethodSource("providerCiclo_HorasTotalesExpected")
    void testGetHorasTotales(Ciclo ciclo, int expected) {
        int horas = Ejercicios.getHorasTotales(Modulos.MODULOS, ciclo);
        System.out.println("El ciclo " + ciclo.nombre() + " tiene " + horas + " horas.");

        assertEquals(expected, horas);
    }




    //---- TEST del EJERCICIO 5

    static Stream<Arguments> testGetHorasPendientes() {
        return Stream.of(
                Arguments.of(Alumnos.ARMANDO, 1040),
                Arguments.of(Alumnos.BELEN, 1136),
                Arguments.of(Alumnos.ESTHER, 1844),
                Arguments.of(Alumnos.AMADOR, 1648)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGetHorasPendientes(Alumno alumno, int expected) {
        int horas = Ejercicios.getHorasPendientes(
                Calificaciones.CALIFICACIONES_SAMPLE, Modulos.MODULOS, alumno, Ciclos.DAM);

        System.out.println(
                String.format("%-25s", alumno.nombre() + " " + alumno.apellidos()) +
                        String.format( "%10s horas",  NumberFormat.getNumberInstance().format(horas)));

        assertThat(horas, is(expected));
    }


    //--- TEST del EJERCICIO 6
    @Test
    void testGetHorasPendientesAlumnosDAM() {
        var result = Ejercicios
                .getHorasPendientesByCiclo(
                        Calificaciones.CALIFICACIONES_SAMPLE,
                        Modulos.MODULOS, Ciclos.DAM
                );

        System.out.printf("%-25s%s\n", "Alumno","Horas pendientes");
        System.out.println("-----------------------------------------");

        result.forEach((alumno, horas) -> {
            System.out.println(
                    String.format("%-25s", alumno.nombre() + " " + alumno.apellidos()) +
                            String.format( "%10s",  NumberFormat.getNumberInstance().format(horas)));
        });

        assertThat(result.keySet(), containsInAnyOrder(Alumnos.BELEN, Alumnos.ESTHER, Alumnos.AMADOR, Alumnos.ARMANDO));
        assertThat(result.get(Alumnos.ARMANDO), is(1040));
        assertThat(result.get(Alumnos.ESTHER), is(1844));
        assertThat(result.get(Alumnos.AMADOR), is(1648));
        assertThat(result.get(Alumnos.BELEN), is(1136));
    }


    //---- TEST del EJERCICIO 7

    static Stream<Arguments> testGetFilteredStream() {
        return Stream.of(
                Arguments.of(Alumnos.ARMANDO, 7),
                Arguments.of(Alumnos.BELEN, 6),
                Arguments.of(Alumnos.ESTHER, 2),
                Arguments.of(Alumnos.AMADOR, 3)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGetFilteredStream(Alumno alumno, int expected) {
        Stream<Calificacion> filteredStream = Ejercicios.getFilteredCalificacionesSupplier(
                Calificaciones.CALIFICACIONES_SAMPLE, alumno).get();

        int count = filteredStream.toList().size();

        System.out.println("count = " + count);

        assertThat(count, is(expected));
    }


    //---- TEST del EJERCICIO 8

    static Stream<Arguments> testFractionToDecimal() {
        return Stream.of(
                Arguments.of(5.6, 7, OptionalDouble.of(5.6 / 7)),
                Arguments.of(7.2, 3, OptionalDouble.of(7.2 / 3)),
                Arguments.of(1, 0, OptionalDouble.empty()));
    }

    @ParameterizedTest
    @MethodSource
    void testFractionToDecimal(double numerator, long denominator, OptionalDouble expected) {
        OptionalDouble result = Ejercicios.fractionToDecimal(numerator, denominator);

        System.out.println(result);
        assertThat(result, is(expected));
    }



    //---- TEST del EJERCICIO 9

    @Test
    void testAveragingWeighted() {
        record Tarea(String nombre, double puntos, long peso) { }

        Stream<Tarea> tareas = Stream.of(
                new Tarea("Tareas para la casa", 5.0, 20),
                new Tarea("Exámenes cortos",4.7, 25),
                new Tarea("Trabajos en grupo", 4.2, 25),
                new Tarea("Examen final", 3.5, 30)
        );

        OptionalDouble result = tareas.collect(
                averagingWeighted(
                        Tarea::puntos,
                        Tarea::peso
                ));

        System.out.println(result);
        assertThat(result, is(OptionalDouble.of(4.275)));
    }



    //---- TEST del EJERCICIO 10

    static Stream<Arguments> testGetNotaMediaExpediente() {
        return Stream.of(
                Arguments.of(Alumnos.ARMANDO, OptionalDouble.of(8.633)),
                Arguments.of(Alumnos.BELEN, OptionalDouble.of(6.148)),
                Arguments.of(Alumnos.ESTHER, OptionalDouble.of(7.0)),
                Arguments.of(Alumnos.AMADOR, OptionalDouble.of(6.091))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testGetNotaMediaExpediente(Alumno alumno, OptionalDouble expected) {
        OptionalDouble average = Ejercicios
                .getNotaMediaExpediente(Calificaciones.CALIFICACIONES_SAMPLE, alumno);

        System.out.println(average);

        assertThat(average.orElse(0), closeTo(expected.orElse(0), 0.0005));
    }



    //---- TEST del EJERCICIO 11

    @Test
    void testGetNotaMediaExpedientesDAM() {
        Map<Alumno, OptionalDouble> map = Ejercicios.
                getNotaMediaExpediente(Calificaciones.CALIFICACIONES_SAMPLE, Ciclos.DAM);


        System.out.printf("%-25s%s\n", "Alumno", "Nota media");
        System.out.println("-----------------------------------------");


        DecimalFormat df = new DecimalFormat("#.000");


        map.forEach((alumno, average) -> {
            System.out.println(
                    String.format("%-25s", alumno.nombre() + " " + alumno.apellidos()) +
                            String.format("%10s", average.isPresent() ?
                                    df.format(average.getAsDouble()) : ""));
        });

        assertThat(map.keySet(),
                contains(Alumnos.ARMANDO, Alumnos.ESTHER, Alumnos.BELEN, Alumnos.AMADOR));

        assertThat(map.get(Alumnos.ARMANDO).orElseThrow(), closeTo(8.633, 0.0005));
        assertThat(map.get(Alumnos.ESTHER).orElseThrow(), closeTo(7.0, 0.0005));
        assertThat(map.get(Alumnos.BELEN).orElseThrow(), closeTo(6.148, 0.0005));
        assertThat(map.get(Alumnos.AMADOR).orElseThrow(), closeTo(6.091, 0.0005));
    }



}
