package de.vommond;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

public class Starter extends AbstractVerticle {
	
	private final Logger logger = LoggerFactory.getLogger(Starter.class);
	
	private HttpServer server;

	
	@Override
	public void start() {
		this.logger.info("start() > enter");

		
		
		/**
		 * Set body and cookie handler
		 */
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create().setMergeFormAttributes(false));
		router.route().handler(CookieHandler.create());
		/**
		 * FIXME: Add here flag to controll the stupid session lookup
		 */
		router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)).setNagHttps(false));
	
	
		
		/**
		 * Static content. Attention must be added to router AFTER
		 * the rest services!
		 */
		initStatic(router);
		

			
		/**
		 * Launch server
		 */
		HttpServerOptions options = new HttpServerOptions()
			.setCompressionSupported(true);
		
		this.server = vertx.createHttpServer(options)
			.requestHandler(router::accept)
			.listen(8080);
		
	
	}



	private void initStatic(Router router) {
		StaticHandler handler = StaticHandler.create();
		
		
		router.route().handler(handler).failureHandler(frc -> {
			
			if(frc.failure()!=null){
				frc.failure().printStackTrace();
			} else {
				System.err.println("StaticHandler.default > " + frc.request().path());
			}
			HttpServerResponse response = frc.response();
			response.sendFile("webroot/index.html");
		});
	}
	
	
	
	
	
	@Override
	public void stop(){
	
		try {
		
			server.close();
		
		
	
			super.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

