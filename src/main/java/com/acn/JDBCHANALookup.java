package com.acn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.acn.utility.CloudConnector;
import com.acn.utility.FormatXML;
import com.acn.utility.XMLUtility;

public class JDBCHANALookup {

	private CloudConnector cloudConnector;
	private Connection connection;

	public JDBCHANALookup(String JDBCURL, String user, String password, Properties connectionProps) throws Exception {

		cloudConnector = new CloudConnector("TCP");

		// Setup JDBC Connection
		if (connectionProps == null)
			connectionProps = new Properties();
		connectionProps.put("user", user);
		connectionProps.put("password", password);
		connectionProps.put("proxyHostname", cloudConnector.getOnpremise_proxy_host());
		connectionProps.put("proxyPort", Integer.toString(cloudConnector.getOnpremise_proxy_tcp_port()));
		connectionProps.put("proxyUserName", cloudConnector.getProxyHeaders().get("Proxy-Authorization"));
		try {
			connection = DriverManager.getConnection(JDBCURL, connectionProps);
		} catch (SQLException e) {
			throw new Exception(e);
		}
	}

	public ResultSet getResultSet(String statement) throws Exception {
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(statement);
			return rs;
		} catch (SQLException e) {
			throw new Exception(e);
		}
	}

	public String getResultSetXML(String statement) throws Exception {
		try {
			ResultSet rs = this.getResultSet(statement);
			XMLUtility xml = new XMLUtility("<ROOT/>");
			Document document = xml.getDocument();
			Element docElement = document.getDocumentElement();

			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();

			while (rs.next()) {
				Element row = document.createElement("select_response");
				docElement.appendChild(row);

				for (int i = 1; i <= colCount; i++) {
					String columnName = rsmd.getColumnName(i);
					Object value = rs.getObject(i);
					Element node = document.createElement(columnName);
					node.appendChild(document.createTextNode(value.toString()));
					row.appendChild(node);
				}
			}
			return xml.getDocumentString();
		} catch (SQLException e) {
			throw new Exception(e);
		}
	}

	public Map<String, List<String>> formatResponseToOutput(String xmlString, List<String> outputList)
			throws Exception {
		return FormatXML.formatResponseToOutput(xmlString, "//select_response", outputList);
	}

	public List<Map<String, String>> formatResponseToOutputRows(String xmlString, List<String> outputList)
			throws Exception {
		return FormatXML.formatResponseToOutputRows(xmlString, "//select_response", outputList);
	}
}
