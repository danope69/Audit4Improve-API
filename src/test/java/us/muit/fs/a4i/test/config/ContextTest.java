/**
 * 
 */
package us.muit.fs.a4i.test.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.awt.Font;
import java.io.File;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import us.muit.fs.a4i.config.Context;
import us.muit.fs.a4i.config.Checker;
import us.muit.fs.a4i.model.entities.Indicator;

/**
 * @author Isabel Román
 *
 */
class ContextTest {
	private static Logger log = Logger.getLogger(CheckerTest.class.getName());
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
	 * Test method for {@link us.muit.fs.a4i.config.Context#getContext()}.
	 */
	@Test
	void testGetContext() {
		Context first = null;
		Context second = null;
		try {
			first = Context.getContext();
			second = Context.getContext();
		} catch (IOException e) {
			fail("No deberia haber problemas con el fichero de configuracion");
			e.printStackTrace();
		}
		assertNotNull(first, "Deberia devolver una instancia de Context");
		assertNotNull(second, "Deberia devolver una instancia de Context");
		assertSame(first, second, "El patron singleton deberia devolver siempre la misma instancia");
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.config.Context#setAppConf(java.lang.String)}.
	 */
	@Test
	void testSetAppConf() {
		File appConf = null;
		try {
			// Se crea un fichero temporal con una propiedad nueva
			// No se sobreescriben las propiedades por defecto para no alterar el resto de tests
			appConf = File.createTempFile("appConf", ".conf");
			try (PrintWriter writer = new PrintWriter(appConf)) {
				writer.println("app.custom.property=testValue");
			}
			Context.setAppConf(appConf.getAbsolutePath());
			assertTrue(Context.getContext().getPropertiesNames().contains("app.custom.property"),
				"La propiedad del fichero de aplicacion deberia aparecer en las propiedades");
		} catch (IOException e) {
			fail("No deberia haber problemas leyendo el fichero de aplicacion");
			e.printStackTrace();
		} finally {
			if (appConf != null && appConf.exists()) {
				appConf.deleteOnExit();
			}
		}
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.config.Context#getChecker()}.
	 */
	@Test
	void testGetChecker() {
		Checker checker = null;
		try {
			checker = Context.getContext().getChecker();
		} catch (IOException e) {
			fail("No deberia haber problemas con el fichero de configuracion");
			e.printStackTrace();
		}
		assertNotNull(checker, "Deberia existir un checker configurado");
		try {
			// Las sucesivas consultas devuelven siempre el mismo checker
			assertSame(checker, Context.getContext().getChecker(), "El checker deberia ser unico");
		} catch (IOException e) {
			fail("No deberia haber problemas con el fichero de configuracion");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.config.Context#getPersistenceType()}.
	 */
	@Test
	void testGetPersistenceType() {
		try {					
			assertEquals("excel",Context.getContext().getPersistenceType(),"Debería estar definido el tipo excel por defecto en a4i.conf");
		} catch (IOException e) {
			fail("El fichero no se localiza");
			e.printStackTrace();
		}		
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.config.Context#getRemoteType()}.
	 */
	@Test
	void testGetRemoteType() {
		try {
			assertEquals("github",Context.getContext().getRemoteType(),"Debería estar definido el tipo github por defecto en a4i.conf");
		} catch (IOException e) {
			fail("El fichero no se localiza");
			e.printStackTrace();
		}		
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.config.Context#getDefaultFont()}.
	 */
	@Test
	void testGetDefaultFont() {
		try {
			Font font = Context.getContext().getDefaultFont();
			assertNotNull(font, "Deberia construir una fuente a partir de las propiedades por defecto");
			assertEquals(12, font.getSize(), "La altura por defecto definida en a4i.conf es 12");
			assertEquals(Font.PLAIN, font.getStyle(), "La fuente por defecto no deberia tener estilo");
		} catch (IOException e) {
			fail("No deberia haber problemas con el fichero de configuracion");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.config.Context#getMetricFont()}.
	 */
	@Test
	void testGetMetricFont() {
		try {
			Font metricFont = Context.getMetricFont();
			assertNotNull(metricFont, "Sin fuente propia para metricas deberia devolver la fuente por defecto");
			assertEquals(Context.getContext().getDefaultFont(), metricFont,
				"Sin fuente definida para metricas debe coincidir con la fuente por defecto");
		} catch (IOException e) {
			fail("No deberia haber problemas con el fichero de configuracion");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.config.Context#getIndicatorFont(us.muit.fs.a4i.model.entities.Indicator.State)}.
	 */
	@Test
	void testGetIndicatorFont() {
		try {
			Font indicatorFont = Context.getIndicatorFont(Indicator.State.OK);
			assertNotNull(indicatorFont, "Deberia devolver una fuente para el estado OK");
			// En el fichero de configuracion no hay fuentes especificas por estado, se usa la fuente por defecto
			assertEquals(Context.getContext().getDefaultFont(), indicatorFont,
				"Sin fuente especifica para el estado se debe usar la fuente por defecto");
		} catch (IOException e) {
			fail("No deberia haber problemas con el fichero de configuracion");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link us.muit.fs.a4i.config.Context#getPropertiesNames()}.
	 */
	@Test
	void testGetPropertiesNames() {
		Set<String> names = null;
		try {
			names = Context.getContext().getPropertiesNames();
		} catch (IOException e) {
			fail("No deberia haber problemas con el fichero de configuracion");
			e.printStackTrace();
		}
		assertNotNull(names, "Deberia devolver el conjunto de propiedades");
		assertFalse(names.isEmpty(), "Deberia haber propiedades definidas");
		assertTrue(names.contains("persistence.type"), "Debe estar la propiedad persistence.type");
		assertTrue(names.contains("remote.type"), "Debe estar la propiedad remote.type");
		assertTrue(names.contains("Font.default.type"), "Deben estar las propiedades de la fuente por defecto");
		assertTrue(names.contains("Font.default.height"), "Deben estar las propiedades de la fuente por defecto");
		assertTrue(names.contains("Font.default.color"), "Deben estar las propiedades de la fuente por defecto");
	}

}

