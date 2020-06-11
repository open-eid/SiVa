package ee.openeid.siva.soaptest;

import io.restassured.internal.path.xml.NodeChildrenImpl;
import io.restassured.path.xml.element.Node;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static java.util.Objects.requireNonNull;

public class XmlNodeContainsKeyMatcher extends TypeSafeMatcher<NodeChildrenImpl> {

    private final String key;

    public XmlNodeContainsKeyMatcher(String key) {
        this.key = requireNonNull(key);
    }

    @Override
    protected boolean matchesSafely(NodeChildrenImpl nodeChildren) {
        if (nodeChildren.isEmpty()) {
            return false;
        }

        for (Node node : nodeChildren.nodeIterable()) {
            if (!containsKey(node)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsKey(Node node) {
        for (Node nodeKey : node.children().nodeIterable()) {
            if (key.equals(nodeKey.name())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("Expected key [%s] to be present, but not found", key));
    }
}
