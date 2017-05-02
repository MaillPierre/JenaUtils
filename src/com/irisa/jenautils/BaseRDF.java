package com.irisa.jenautils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

//import org.apache.jena.atlas.web.auth.ServiceAuthenticator;
import org.apache.log4j.Logger;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

/**
 * Classe utilistaire pour la connection à un serveur.
 * La classe en configurable pour un acces distant via http ou local par le chargement d'un fichier dans Jena
 * elle permet également de disposer de fonctions pour le voisinage, le nombre d'occurence et autres fonctions utilitaires pour les requêtes
 * @author maillot
 *
 */
public class BaseRDF {
	
    private static Logger logger = Logger.getLogger(BaseRDF.class);

	private String _server;
	private MODE _mode;
	private Model _model;
//	private ServiceAuthenticator _auth;
	
	public enum MODE 
	{
		LOCAL, DISTANT;
	}
	
	public BaseRDF(String adresse, MODE mode)
	{
		this(adresse, null, mode);
	}
	
	public BaseRDF(String adresse, String opt, MODE mode)
	{
		this._mode = mode;
//		this._auth = new ServiceAuthenticator();
		if(mode == MODE.DISTANT)
		{
			this._server = adresse;
			this._model = null;
		}
		else
		{
			this._server = null;
			this._model = ModelFactory.createDefaultModel();
			if(adresse != null)
			{
				try {
					FileInputStream fileStr = new FileInputStream(adresse);
					this._model.read(fileStr, null, Utils.guessLangFromFilename(adresse) );
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public BaseRDF(String adresse)
	{
		this(adresse, MODE.LOCAL);
	}
	
	/**
	 * Par défaut, mode local, model vide
	 */
	public BaseRDF() {
		this(null, MODE.LOCAL);
	}
	
	public long size()
	{
		if(this._mode == MODE.DISTANT)
		{
			return -1;
		}
		else
		{
			return this._model.size();
		}
	}
	
	public void setDistantServer(String server)
	{
		this._server = server;
	}
	
	public void setLocalMode()
	{
		if(this._model == null)
		{
			this._model = ModelFactory.createDefaultModel();
		}
		this._mode = MODE.LOCAL;
	}
	
	public void setLocalMode(Model model)
	{
		if(this._model != null)
		{
			this._model.close();
		}
		this._mode = MODE.LOCAL;
		this._model = model;
	}
	
	public void setDistantMode(String server)
	{
		this._server = server;
		this._mode = MODE.DISTANT;
	}
	
	public QueryExecution executionQuery(String query)
	{
		Query nQuery = QueryFactory.create(query, Syntax.syntaxSPARQL_11);
		return executionQuery(nQuery);
	}

	public QueryExecution executionQuery(Query query) 
	{
		logger.trace("executeQuery(Query query)");
		QueryExecution result = null;
		
		try
		{
			if(_mode == MODE.DISTANT) 
			{
				result = QueryExecutionFactory.sparqlService(_server, query/*, this._auth*/);
			}
			else 
			{
				result = QueryExecutionFactory.create(query, _model);
			}
		}
		catch(QueryParseException e)
		{
			logger.error("Requete mal formée - " + query);
			e.printStackTrace();
			System.out.println(query);
		}
		result.setTimeout(30000, TimeUnit.MILLISECONDS);
		logger.trace("FIN executeQuery(Query query)");
		return result;
	}
	
	public String toString() {
		return this._mode + " : " + this._server + " : " + this.size();
	}

	
}
