package us.muit.fs.a4i.test.model.entities;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;


import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


import us.muit.fs.a4i.control.IndicatorsCalculator;
import us.muit.fs.a4i.exceptions.IndicatorException;
import us.muit.fs.a4i.model.entities.Indicator;
import us.muit.fs.a4i.model.entities.Metric;
import us.muit.fs.a4i.model.entities.Report;
import us.muit.fs.a4i.model.entities.ReportI;

import org.junit.jupiter.api.DisplayName;


/**
 * <p>Test para probar la clase Report</p>
 * @author Isabel Román
 *
 */
@ExtendWith(MockitoExtension.class)
class ReportTest {
	private static Logger log=Logger.getLogger(ReportTest.class.getName());
	/**
	 * <p>Objetos tipo Mock, sustitutos de las clases de las que depende Report</p>
	 * 
	 */
	@Mock(serializable =true)
	private static IndicatorsCalculator indCalcMock= Mockito.mock(IndicatorsCalculator.class);
		
	
	//Servirán para conocer el argumento con el que se ha invocado algún método de alguno de los mocks (sustitutos o representantes)
    //ArgumentCaptor es un genérico, indico al declararlo el tipo del argumento que quiero capturar
	@Captor
	private ArgumentCaptor<Integer> intCaptor;
	@Captor
	private ArgumentCaptor<String> strCaptor;
	@Captor
	private ArgumentCaptor<Metric> metricCaptor;
	@Captor
	private ArgumentCaptor<Indicator> indicatorCaptor;
	@Captor
	private ArgumentCaptor<Report> reportCaptor;
	
	@Mock(serializable = true)
	private static Metric<Integer> metricIntMock= Mockito.mock(Metric.class);
	@Mock(serializable = true)
	private static Metric<Date> metricDatMock= Mockito.mock(Metric.class);
	@Mock(serializable = true)
	private static Indicator<Integer> indicatorIntMock= Mockito.mock(Indicator.class);
	

	private static Report reportTested;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test del constructor simple
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#Report()}.
	 */
	@Test
	void testReport() {
		reportTested = new Report();
		// Un informe recien creado no tiene metricas ni indicadores, id ni tipo
		assertNotNull(reportTested.getAllMetrics(), "Deberia existir la coleccion de metricas");
		assertTrue(reportTested.getAllMetrics().isEmpty(), "Un informe recien creado no tiene metricas");
		assertNull(reportTested.getMetricByName("issues"), "Un informe vacio no tiene la metrica issues");
		assertNull(reportTested.getIndicatorByName("overdued"), "Un informe vacio no tiene indicadores");
		assertNull(reportTested.getId(), "Un informe recien creado no tiene identificador");
		assertNull(reportTested.getType(), "Un informe recien creado no tiene tipo");
	}

	/**
	 * Test del constructor pasándole el id
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#Report(java.lang.String)}.
	 */
	@Test
	void testReportString() {
		reportTested=new Report("id");
		
		assertEquals("id",reportTested.getId(),"No se establece correctamente el identificador del informe");
		
		
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#getMetricByName(java.lang.String)}.
	 */
	@Test
	void testGetMetricByName() {
		reportTested = new Report();
		setMetricsMocks();
		// Al no estar anadidas las metricas no se localizan
		assertNull(reportTested.getMetricByName("issues"), "Sin anadir la metrica no deberia existir");
		// Se anade la metrica de tipo Integer y se localiza por su nombre
		reportTested.addMetric(metricIntMock);
		Metric metric = reportTested.getMetricByName("issues");
		assertNotNull(metric, "Deberia localizar la metrica anadida");
		assertSame(metricIntMock, metric, "Deberia devolver la misma metrica anadida");
		assertEquals(metricIntMock.getValue(), metric.getValue(), "Deberia tener el valor especificado en el mock");
		assertEquals(metricIntMock.getDescription(), metric.getDescription(),
			"Deberia tener la descripcion especificada en el mock");
		// Igualmente con la metrica de tipo Date
		reportTested.addMetric(metricDatMock);
		metric = reportTested.getMetricByName("lastPush");
		assertNotNull(metric, "Deberia localizar la metrica anadida");
		assertSame(metricDatMock, metric, "Deberia devolver la misma metrica anadida");
		assertEquals(metricDatMock.getValue(), metric.getValue(), "Deberia tener el valor especificado en el mock");
		assertEquals(metricDatMock.getDescription(), metric.getDescription(),
			"Deberia tener la descripcion especificada en el mock");
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#addMetric(us.muit.fs.a4i.model.entities.Metric)}.
	 */
	@Test
	void testAddMetric() {
		reportTested=new Report();	
		setMetricsMocks();		
		//Primero se prueba a añadir una métrica de tipo Integer
		reportTested.addMetric(metricIntMock);
		//Verifico que se ha consultado el nombre una vez al invocar este método, se usa como clave para meterlo en un mapa, hay que consultarlo
		//¿Por qué falla? ¿Con qué no había contado? ¿Hay problemas en el test o en el código?
		//Prueba a sustituir por la línea comentada
		Mockito.verify(metricIntMock).getName();
		//Mockito.verify(metricIntMock, atLeast(1)).getName();
		Metric metric=reportTested.getMetricByName("issues");
		assertEquals(metric.getValue(),3,"Debería tener el valor especificado en el mock");
		assertEquals(metric.getDescription(),"Tareas sin finalizar en el repositorio","Debería tener el valor especificado en el mock");
		
		//Ahora se prueba una métrica de tipo Date
		reportTested.addMetric(metricDatMock);
		metric=reportTested.getMetricByName("lastPush");
		assertEquals(metric.getValue(),metricDatMock.getValue(),"Debería tener el valor especificado en el mock");
		assertEquals(metric.getDescription(),"Último push realizado en el repositorio","Debería tener el valor especificado en el mock");
		
		//Ahora se prueba a añadir otra vez la misma métrica pero con otro valor		
		reportTested.addMetric(metricIntMock);
		Mockito.when(metricIntMock.getValue()).thenReturn(55);	
		metric=reportTested.getMetricByName("issues");
		assertEquals(metric.getValue(),55,"Debería tener el valor especificado en el mock");
		assertEquals(metric.getDescription(),"Tareas sin finalizar en el repositorio","Debería tener el valor especificado en el mock");
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#getIndicatorByName(java.lang.String)}.
	 */
	@Test
	void testGetIndicatorByName() {
		reportTested = new Report();
		// Al no estar anadido el indicador no se localiza
		assertNull(reportTested.getIndicatorByName("overdued"), "Sin anadir el indicador no deberia existir");
		String indicatorName = "overdued";
		Mockito.when(indicatorIntMock.getName()).thenReturn(indicatorName);
		reportTested.addIndicator(indicatorIntMock);
		Indicator indicator = reportTested.getIndicatorByName(indicatorName);
		assertNotNull(indicator, "Deberia localizar el indicador anadido");
		assertSame(indicatorIntMock, indicator, "Deberia devolver el mismo indicador anadido");
		// Los nombres que no estan anadidos tampoco se localizan
		assertNull(reportTested.getIndicatorByName("noexiste"), "Un indicador no anadido no deberia existir");
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#addIndicator(us.muit.fs.a4i.model.entities.Indicator)}.
	 */
	@Test
	void testAddIndicator() {
		reportTested = new Report();
		String indicatorName = "overdued";
		Mockito.when(indicatorIntMock.getName()).thenReturn(indicatorName);
		// El informe almacena el indicador usando su nombre como clave
		reportTested.addIndicator(indicatorIntMock);
		Mockito.verify(indicatorIntMock).getName();
		Indicator indicator = reportTested.getIndicatorByName(indicatorName);
		assertSame(indicatorIntMock, indicator, "El indicador anadido debe localizarse por su nombre");
		assertNull(reportTested.getIndicatorByName("noexiste"), "Un indicador no anadido no deberia existir");
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#calcIndicator(java.lang.String)}.
	 */
	@Test
	void testCalcIndicator() {
		//Definimos calculadora de tipo repositorio
		Mockito.when(indCalcMock.getReportType()).thenReturn(Report.Type.REPOSITORY);
		
		
		//Se configura la calculadora de indicadores del informe
		try {
			reportTested.setIndicatorsCalculator(indCalcMock);
			Mockito.verify(indCalcMock).getReportType();
		} catch (IndicatorException e1) {
			fail("No debería lanzar la excepción");
		}
		//Se solicita el cálculo de un indicador determinado
		reportTested.calcIndicator("issues");
		//Se observa con qué parámetros se invoca a la calculadora de indicadores
		try {
			Mockito.verify(indCalcMock).calcIndicator(strCaptor.capture(), reportCaptor.capture());
			//Elimine el comentario que aparece a continuación, ejecute el test y explique por qué falla
			//Mockito.verify(indCalcMock).calcAllIndicators(reportTested);
		} catch (IndicatorException e) {
			fail("No debería lanzar la excepción");
		}
		
		//Se verfica que se usa el nombre correcto y se pasa la referencia al informe correcto
		assertEquals("issues",strCaptor.getValue(),"Se solicita el cálculo de la métrica adecuada");
		assertEquals(reportTested,reportCaptor.getValue(),"Se pasa la referencia correcta del informe");
		//Hago un test que asegure que el propio informe captura y gestiona la excepción de que el indicador no existe
		try {
			Mockito.doThrow(new IndicatorException("El indicador no existe")).when(indCalcMock).calcIndicator("indKO", reportTested);
			reportTested.calcIndicator("indKO");			
		
		} catch (IndicatorException e) {
			fail("La excepción la debe gestionar el propio informe");
		}    
		
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#setId(java.lang.String)}.
	 */
	@Test
	void testSetId() {
		reportTested = new Report();
		// Un informe recien creado no tiene identificador
		assertNull(reportTested.getId(), "Un informe recien creado no tiene identificador");
		reportTested.setId("idPrueba");
		assertEquals("idPrueba", reportTested.getId(), "setId deberia establecer el identificador");
		// El identificador se puede actualizar
		reportTested.setId("otroId");
		assertEquals("otroId", reportTested.getId(), "setId deberia actualizar el identificador");
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#getId()}.
	 */
	@Test
	void testGetId() {
		// El identificador establecido en el constructor se consulta con getId
		reportTested = new Report("idPrueba");
		assertEquals("idPrueba", reportTested.getId(),
			"getId deberia devolver el identificador establecido en el constructor");
		// Un informe sin identificador establecido devuelve null
		reportTested = new Report();
		assertNull(reportTested.getId(), "Un informe sin identificador deberia devolver null");
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#setIndicatorsCalculator(us.muit.fs.a4i.control.IndicatorsCalculator)}.
	 */
	@Test
	void testSetIndicatorsCalculator() {
		//Definimos calculadora de tipo repositorio
		Mockito.when(indCalcMock.getReportType()).thenReturn(ReportI.Type.REPOSITORY);
		ReportI orgReport=new Report(ReportI.Type.ORGANIZATION);
		ReportI repoReport=new Report(ReportI.Type.REPOSITORY);
		ReportI report=new Report();
		//Vamos a probar establecer la calculadora en un informe que no tiene el tipo aún establecido (Debería tener el tipo de la calculadora al final)
		//Para ello usamos report
		try {
			report.setIndicatorsCalculator(indCalcMock);
			//Se ha tenido que consultar el tipo de calculadora
			Mockito.verify(indCalcMock).getReportType();
			assertEquals(indCalcMock.getReportType(),report.getType());
		} catch (IndicatorException e) {
			fail("No debería lanzar excepción");
		}
		
		//Vamos a probar a establecer la calculadora si el tipo de ambos coincide, uso repoReport
		try {
			repoReport.setIndicatorsCalculator(indCalcMock);
			//Se ha tenido que consultar el tipo de calculadora
			//Mockito.verify(indCalcMock, times(2)).getReportType();
			assertEquals(indCalcMock.getReportType(),repoReport.getType());
		} catch (IndicatorException e) {
			fail("No debería lanzar excepción");
		}
		
		//Vamos a probar a establecer la calculadora si el tipo de la calculadora discrepa con el tipo del informe, uso orgReport
	
		try {
			orgReport.setIndicatorsCalculator(indCalcMock);
			//Se ha tenido que consultar el tipo de calculadora
			fail("Debe saltar una excepción antes, no debería llegar aquí");
		} catch (IndicatorException e) {
			
			log.info("Ha saltado la excepción de indicador");
			//Suponga que los requisitos cambian, le piden que el mensaje debe ser "El tipo de la calculadora discrepa del tipo del informe"
			//Cambie el test para que lo verifique y ejecute ¿Qué ocurre?
			assertEquals(e.getMessage(),"La calculadora no concuerda con el tipo de informe","El mensaje es correcto");
		} catch(Exception e) {
			fail("La excepción no es del tipo IndicatorException como se esperaba");
		}
		//Esta verificación es para mostrar que se puede analizar también el comportamiento interno de la clase
		//en esta ocasión el número de veces que invoca a la calculadora durante el test
		//Probar a cambiar 5 por otro número y ejecutar
		Mockito.verify(indCalcMock,times(5)).getReportType();
	
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#toString()}.
	 */
	@Test
	void testToString() {
		reportTested = new Report();
		Mockito.when(metricIntMock.getName()).thenReturn("issues");
		Mockito.when(indicatorIntMock.getName()).thenReturn("overdued");
		reportTested.addMetric(metricIntMock);
		reportTested.addIndicator(indicatorIntMock);
		String report = reportTested.toString();
		assertNotNull(report, "toString no deberia devolver null");
		assertTrue(report.contains("issues"), "La descripcion del informe deberia contener la metrica issues");
		assertTrue(report.contains("overdued"), "La descripcion del informe deberia contener el indicador overdued");
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.model.entities.Report#getAllMetrics()}.
	 */
	@Test
	void testGetAllMetrics() {
		reportTested = new Report();
		Mockito.when(metricIntMock.getName()).thenReturn("issues");
		Mockito.when(metricDatMock.getName()).thenReturn("lastPush");
		reportTested.addMetric(metricIntMock);
		reportTested.addMetric(metricDatMock);
		Collection<Metric> metricsReport = reportTested.getAllMetrics();
		assertNotNull(metricsReport, "Deberia devolver la coleccion de metricas");
		assertEquals(2, metricsReport.size(), "Deberia contener el numero de metricas anadidas");
		assertTrue(metricsReport.contains(metricIntMock), "La coleccion deberia contener la metrica de tipo Integer");
		assertTrue(metricsReport.contains(metricDatMock), "La coleccion deberia contener la metrica de tipo Date");
	}
	void setMetricsMocks() {
		Date date=Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
		Mockito.when(metricIntMock.getName()).thenReturn("issues");
		Mockito.when(metricIntMock.getDescription()).thenReturn("Tareas sin finalizar en el repositorio");	
		Mockito.when(metricIntMock.getValue()).thenReturn(3);	
	
		Mockito.when(metricDatMock.getName()).thenReturn("lastPush");
		Mockito.when(metricDatMock.getDescription()).thenReturn("Último push realizado en el repositorio");	
		Mockito.when(metricDatMock.getValue()).thenReturn(date);
	}

}

