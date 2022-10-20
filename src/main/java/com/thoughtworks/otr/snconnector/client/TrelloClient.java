package com.thoughtworks.otr.snconnector.client;

import com.julienvey.trello.Trello;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.JDKTrelloHttpClient;
import com.thoughtworks.otr.snconnector.configuration.TrelloConfiguration;
import lombok.Getter;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
public class TrelloClient {
    private final TrelloConfiguration trelloConfiguration;
    private final Trello trelloApi;
    private final RestTemplate restTemplate;

    public TrelloClient(TrelloConfiguration trelloConfiguration) {
        this.trelloConfiguration = trelloConfiguration;
        this.trelloApi = new TrelloImpl(trelloConfiguration.getTrelloApiKey(), trelloConfiguration.getTrelloApiToken(), new JDKTrelloHttpClient());
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(8000);
        httpRequestFactory.setReadTimeout(10000);
        this.restTemplate = new RestTemplate(httpRequestFactory);
    }

    public UriComponentsBuilder buildFullURLBuilder(String url) {
        return UriComponentsBuilder.fromUriString(url)
                                   .queryParam("key", trelloConfiguration.getTrelloApiKey())
                                   .queryParam("token", trelloConfiguration.getTrelloApiToken());
    }
}
