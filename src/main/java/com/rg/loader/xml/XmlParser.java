package com.rg.loader.xml;

import com.rg.servlet.context.JerryServletContext;
import com.rg.servlet.registration.JerryFilterRegistration;
import com.rg.servlet.registration.JerryServletRegistration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.DispatcherType;
import javax.servlet.HttpMethodConstraintElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

class XmlParser {

    private static final Logger LOG = LogManager.getLogger(XmlParser.class);

    public JerryServletContext parseWebXml(File file, ClassLoader classLoader, Set<String> resources, Map<String, JerryServletContext> contexts) {

        JerryServletContext servletContext = new JerryServletContext(classLoader, contexts, resources);

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document web = documentBuilder.parse(file);

            Node root = web.getDocumentElement();
            NodeList childNodes = root.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                switch (node.getNodeName()) {
                    case "context-param": {
                        collectParams(servletContext, node);
                        break;
                    }
                    case "servlet": {
                        collectServlets(servletContext, node);
                        break;
                    }
                    case "servlet-mapping": {
                        collectServletMapping(servletContext, node);
                        break;
                    }
                    case "filter": {
                        collectFilters(servletContext, node);
                        break;
                    }
                    case "filter-mapping": {
                        collectFilterMapping(servletContext, node);
                        break;
                    }
                    case "listener": {
                        collectListeners(classLoader, servletContext, node);
                        break;
                    }
                    case "security-constraint": {
                        break;
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            LOG.error("Parser error.", e);
            throw new IllegalStateException(e.getCause());
        } catch (SAXException e) {
            LOG.error("Parser error.", e);
            throw new IllegalStateException(e.getCause());
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new IllegalStateException(e.getCause());
        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage());
            throw new IllegalStateException(e.getCause());
        } catch (InstantiationException e) {
            LOG.error(e.getMessage());
            throw new IllegalStateException(e.getCause());
        } catch (ClassNotFoundException e) {
            LOG.error("Class not found exception.", e);
            throw new IllegalStateException(e.getCause());
        }

        return servletContext;
    }

    private void collectListeners(ClassLoader classLoader, JerryServletContext servletContext, Node node) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        NodeList listener = node.getChildNodes();

        for (int i = 0; i < listener.getLength(); i++) {
            Node listenerClass = listener.item(i);

            switch (listenerClass.getNodeName()) {
                case "listener-class": {
                    Class<? extends EventListener> clazz = (Class<? extends EventListener>) classLoader.loadClass(listenerClass.getTextContent());
                    servletContext.addListener(clazz);
                }
            }
        }
    }

    private void collectFilters(JerryServletContext servletContext, Node obj) {
        NodeList params = obj.getChildNodes();

        String filterName = null;
        String filterClass = null;
        Map<String, String> initParams = new HashMap<>();

        for (int j = 0; j < params.getLength(); j++) {
            Node node = params.item(j);

            switch (node.getNodeName()) {
                case "filter-name": {
                    filterName = node.getTextContent();
                    break;
                }
                case "filter-class": {
                    filterClass = node.getTextContent();
                    break;
                }
                case "init-param": {
                    KeyValuePair pair = collectParams(node);
                    initParams.put(pair.getKey(), pair.getValue());
                    break;
                }
            }
        }

        JerryFilterRegistration jerryFilterRegistration = new JerryFilterRegistration(filterName, filterClass);
        jerryFilterRegistration.setInitParameters(initParams);

        servletContext.addFilterRegistration(jerryFilterRegistration);
    }

    private void collectFilterMapping(JerryServletContext servletContext, Node mapping) {
        NodeList urls = mapping.getChildNodes();

        String filterName = null;
        Set<String> filterPatterns = new HashSet<>();
        Set<String> servletNames = new HashSet<>();
        Set<DispatcherType> dispatcherTypeSet = new HashSet<>();

        for (int i = 0; i < urls.getLength(); i++) {
            Node url = urls.item(i);
            switch (url.getNodeName()) {
                case "filter-name":
                    filterName = url.getTextContent();
                    break;
                case "url-pattern":
                    filterPatterns.add(url.getTextContent());
                    break;
                case "servlet-name": {
                    servletNames.add(url.getTextContent());
                    break;
                }
                case "dispatcher": {
                    dispatcherTypeSet.add(DispatcherType.valueOf(url.getTextContent()));
                    break;
                }
            }
        }

        if (dispatcherTypeSet.isEmpty()) {
            servletContext.getFilterRegistration(filterName).addMappingForUrlPatterns(null,
                    false, filterPatterns.toArray(new String[filterPatterns.size()]));

            servletContext.getFilterRegistration(filterName).addMappingForServletNames(null,
                    false, servletNames.toArray(new String[servletNames.size()]));
        } else {
            servletContext.getFilterRegistration(filterName).addMappingForUrlPatterns(EnumSet.copyOf(dispatcherTypeSet),
                    false, filterPatterns.toArray(new String[filterPatterns.size()]));

            servletContext.getFilterRegistration(filterName).addMappingForServletNames(EnumSet.copyOf(dispatcherTypeSet),
                    false, servletNames.toArray(new String[servletNames.size()]));
        }
    }

    private void collectServlets(JerryServletContext servletContext, Node obj) {
        NodeList params = obj.getChildNodes();

        String servletName = null;
        String servletClass = null;
        Map<String, String> initParams = new HashMap<>();
        int loadOnStartup = -1;

        for (int j = 0; j < params.getLength(); j++) {
            Node node = params.item(j);

            switch (node.getNodeName()) {
                case "servlet-name": {
                    servletName = node.getTextContent();
                    break;
                }
                case "servlet-class": {
                    servletClass = node.getTextContent();
                    break;
                }
                case "init-param": {
                    KeyValuePair pair = collectParams(node);
                    initParams.put(pair.getKey(), pair.getValue());
                    break;
                }
            }
        }

        JerryServletRegistration jerryServletRegistration = new JerryServletRegistration(servletName, servletClass);
        jerryServletRegistration.setInitParameters(initParams);
        jerryServletRegistration.setLoadOnStartup(loadOnStartup);

        servletContext.addServletRegistration(jerryServletRegistration);
    }

    private void collectServletMapping(JerryServletContext servletContext, Node mapping) {
        NodeList urls = mapping.getChildNodes();

        String servletName = null;
        Set<String> servletPatterns = new HashSet<>();

        for (int i = 0; i < urls.getLength(); i++) {
            Node url = urls.item(i);
            switch (url.getNodeName()) {
                case "servlet-name":
                    servletName = url.getTextContent();
                    break;
                case "url-pattern":
                    servletPatterns.add(url.getTextContent());
                    break;
            }
        }

        servletContext.getServletRegistration(servletName).addMapping(servletPatterns.toArray(new String[servletPatterns.size()]));
    }

    private void collectParams(JerryServletContext servletContext, Node parameters) {
        KeyValuePair pair = collectParams(parameters);
        servletContext.setInitParameter(pair.getKey(), pair.getValue());
    }

    private KeyValuePair collectParams(Node parameters) {
        NodeList params = parameters.getChildNodes();

        String name = null;
        String value = null;

        for (int i = 0; i < params.getLength(); i++) {
            Node param = params.item(i);
            switch (param.getNodeName()) {
                case "param-name":
                    name = param.getTextContent();
                    break;
                case "param-value":
                    value = param.getTextContent();
                    break;
            }
        }
        return new KeyValuePair(name, value);
    }

    private void collectSecurityConstraint(Node constraint) {
        NodeList elements = constraint.getChildNodes();

        Set<String> roles = null;
        String transportGuarantee = null;

        for (int i = 0; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            switch (element.getNodeName()) {
                case "web-resource-collection": {
                    collectWebResourceCollection(element);

                    break;
                }
                case "auth-constraint": {
                    roles = collectAuthConstraint(element);
                    break;
                }
                case "user-data-constraint": {
                    transportGuarantee = collectUserDataConstraint(element);
                    break;
                }
            }
        }
    }

    private HttpMethodConstraintElement collectWebResourceCollection(Node resourceCollection) {
        NodeList elements = resourceCollection.getChildNodes();

        String name = null;
        Set<String> urls = new HashSet<>();
        Set<String> methods = new HashSet<>();

        for (int i = 0; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            switch (element.getNodeName()) {
                case "web-resource-name": {
                    name = element.getTextContent();
                    break;
                }
                case "url-pattern": {
                    urls.add(element.getTextContent());
                    break;
                }
                case "http-method": {
                    methods.add(element.getTextContent());
                    break;
                }
            }
        }

        return null;
    }

    private Set<String> collectAuthConstraint(Node auth) {
        NodeList elements = auth.getChildNodes();

        Set<String> roles = new HashSet<>();

        for (int i = 0; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            switch (element.getNodeName()) {
                case "role-name": {
                    roles.add(element.getTextContent());
                    break;
                }
            }
        }

        return roles;
    }

    private String collectUserDataConstraint(Node data) {
        NodeList elements = data.getChildNodes();

        String transportGuarantee = null;

        for (int i = 0; i < elements.getLength(); i++) {
            Node element = elements.item(i);
            switch (element.getNodeName()) {
                case "transport-guarantee": {
                    transportGuarantee = element.getTextContent();
                    break;
                }
            }
        }

        return transportGuarantee;
    }
}