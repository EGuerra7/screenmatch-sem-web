package br.com.guerra.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.guerra.screenmatch.model.DadosEpisodio;
import br.com.guerra.screenmatch.model.DadosSerie;
import br.com.guerra.screenmatch.model.DadosTemporada;
import br.com.guerra.screenmatch.service.ConsumoApi;
import br.com.guerra.screenmatch.service.ConverteDados;

public class Principal {

    private Scanner leitura = new Scanner(System.in);

    private ConsumoApi consumoApi = new ConsumoApi();

    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=74170ce1";

    public void exibeMenu(){
        System.out.println("Digite o nome da serie para a busca:");
        var serie = leitura.nextLine();
		var json = consumoApi.obterDados(ENDERECO + serie.replace(" ", "+") + API_KEY);
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 1; i <= dados.totalTemporadas(); i++){
			json = consumoApi.obterDados(ENDERECO + serie.replace(" ", "+") + "&season=" + i + API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);

        // for (int i = 0; i < dados.totalTemporadas(); i++) {
        //     List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios(); 
        //     for (int j = 0; j < episodiosTemporada.size(); j++){
        //         System.out.println(episodiosTemporada.get(j).titulo());
        //     }
        // }

        temporadas.forEach(t -> t.episodios().forEach((e -> System.out.println(e.titulo()))));
        
        // List<String> nomes = Arrays.asList("Erick", "João", "Vinicíus", "Felipe", "Miguel");

        // nomes.stream().sorted().limit(3).filter(n -> n.startsWith("E")).map(n -> n.toUpperCase()).forEach(System.out::println);

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
            .flatMap(t -> t.episodios().stream())
            .collect(Collectors.toList());  

        System.out.println("\nTop 10 de avaliação dos episodios:");
        dadosEpisodios.stream()
            .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
            .peek(e -> System.out.println("Primeiro Filtro (N/A) " + e))
            .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
            .peek(e -> System.out.println("Ordenação " + e))
            .limit(10)
            .peek(e -> System.out.println("Limite " + e))
            .map(e -> e.titulo().toUpperCase())
            .peek(e -> System.out.println("Mapeamento " + e))
            .forEach(System.out::println);

        
        // List<Episodio> episodios = temporadas.stream()
        // .flatMap(t -> t.episodios().stream()
        //         .map(d -> new Episodio(t.numero(), d))        
        //         ).collect(Collectors.toList());

        // episodios.forEach(System.out::println);

        // System.out.println("A partir de que ano você deseja ver os episódios? ");
        // var ano = leitura.nextInt();
        // leitura.nextLine();

        // LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        // DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // episodios.stream()
        //     .filter(e -> e.getDataDeLancamento() != null && e.getDataDeLancamento().isAfter(dataBusca))
        //     .forEach(e -> System.out.println("Temporada: " + e.getTemporada() + 
        //                                     " Episódio: " + e.getTitulo() + 
        //                                     " Data de Lançamento: " + e.getDataDeLancamento().format(formatador)
        //     ));

    }
}
