#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}

import org.junit.Before
import org.junit.Test

class HomeControllerTest {

    HomeController controller

    @Before
    void createController() {
        controller = new HomeController()
    }

    @Test
    void indexShouldReturnCorrectViewName() {
        assert controller.index() == "index"
    }
}
