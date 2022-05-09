package com.github.fribourgsdp.radio.data

import com.github.fribourgsdp.radio.data.Genre.*

object StarterPlaylists {
    val top100french = Playlist("Chanson française", FRENCH)
    val basicalBlindTest = Playlist("Basical blind test", NONE)
    val movieThemeSongs = Playlist("Movie theme songs", MOVIE)
    val videoGames = Playlist("Video game songs", VIDEO_GAMES)
    val classicalHits = Playlist("Classical music hits", CLASSICAL)
    init {
        addArtistSongToPlaylist(top100french,
            "Indochine" to "L\'aventurier",
            "Jean-Jacques Goldman, Michael Jones" to "Je te donne",
            "Marc Lavoine" to  "Elle a les yeux revolver",
            "Jacques Brel" to "Ne me quitte pas",
            "Charles Trenet" to "La mer",
            "Barbara" to "L’aigle noir",
            "Edith Piaf" to "Hymne à l’amour",
            "Léo Ferré" to "Avec le temps",
            "Yves Montand" to "Les feuilles mortes",
            "Georges Brassens" to "Les copains d’abord",
            "Charles Aznavour" to "La bohème",
            "Georges Brassens" to "Chanson pour l’Auvergnat",
            "Céline Dion" to "Pour que tu m’aimes encore",
            "Yves Duteil" to "Prendre un enfant",
            "Jacques Brel" to "Quand on n’a que l’amour",
            "Henri Salvador" to "Syracuse",
            "Claude François" to "Comme d’habitude",
            "Jacques Brel" to "Le plat pays",
            "Edith Piaf" to "La vie en rose",
            "Jean Ferrat" to "La montagne",
            "Jacques Dutronc" to "Il est cinq heures Paris s’eveille",
            "Jacques Brel" to "Amsterdam",
            "Yves Duteil" to "La langue de chez nous",
            "Beau Dommage" to "La complainte du phoque en Alaska",
            "Nino Ferrer" to "Le sud",
            "Michel Sardou" to "La maladie d’amour",
            "Edith Piaf" to "Milord",
            "Gilbert Bécaud" to "Nathalie",
            "Joe Dassin" to "L’été indien",
            "Serge Gainsbourg" to "La Javanaise",
            "Francis Cabrel" to "Petite Marie",
            "Francis Cabrel" to "L’encre de tes yeux",
            "Jacques Brel" to "Les vieux",
            "Claude Dubois" to "Le blues du businessman",
            "Edith Piaf" to "Non, je ne regrette rien",
            "Léo Ferré" to "C’est extra",
            "Yves Duteil" to "Pour les enfants du monde entier",
            "Cora Vaucaire" to "Le temps des cerises",
            "Johnny Hallyday" to "Que je t’aime",
            "Fabienne Thibeault" to "Les uns contre les autres",
            "Jean Ferrat" to "Que serais-je sans toi?",
            "Francis Cabrel" to "Je l’aime à mourir",
            "Serge Gainsbourg" to "La chanson de Prévert",
            "Georges Brassens" to "Les amoureux des bancs publics",
            "Serge Gainsbourg" to "Le poinçonneur des Lilas",
            "Jean-Jacques Goldman" to "Là-bas",
            "Joe Dassin" to "Les Champs-Elysées",
            "Laurent Voulzy" to "Belle-Île-En-Mer…",
            "Gérard Lenorman" to "La ballade des gens heureux",
            "Raymond Levesque" to "Quand les hommes vivront d’amour",
            "Alain Souchon" to "Foule sentimentale",
            "Edith Piaf" to "La foule",
            "Félix Leclerc" to "Le petit bonheur",
            "Daniel Guichard" to "Mon vieux",
            "Claude Léveillée" to "Frédéric",
            "Daniel Lavoie" to "Ils s’aiment",
            "Julien Clerc" to "Ma préférence",
            "Daniel Balavoine" to "L’Aziza",
            "Barbara" to "Ma plus belle histoire d’amour",
            "Hugues Aufray" to "Céline",
            "Charles Trenet" to "L’âme des poètes",
            "Robert Charlebois" to "Lindberg",
            "Maxime Le Forestier" to "San Francisco",
            "Lucienne Boyer" to "Parlez-moi d’amour",
            "Boris Vian" to "Le déserteur",
            "Gilles et les Compagnons" to "Les trois cloches",
            "Nino Ferrer" to "La maison près de la fontaine",
            "Jean Ferrat" to "C’est beau la vie",
            "Françoise Hardy" to "Message personnel",
            "Félix Leclerc" to "L’hymne au printemps",
            "Gilles Vigneault" to "Mon pays",
            "Guy Béart" to "L’eau vive",
            "Tino Rossi" to "Petit Papa Noël",
            "Christophe" to "Aline",
            "Salvatore Adamo" to "Inch’ Allah",
            "Gilbert Bécaud" to "Et maintenant",
            "Lucille Dumont" to "Le ciel se marie avec la mer",
            "Berthe Sylva" to "Les roses blanches",
            "Michel Fugain" to "Une belle histoire",
            "Julien Clerc" to "Femmes je vous aime",
            "Jean-Pierre Ferland" to "Je reviens chez nous",
            "Serge Gainsbourg" to "Je suis venu te dire",
            "Georges Moustaki" to "Il y avait un jardin",
            "Fabienne Thibeault" to "Ziggy",
            "Claude Nougaro" to "Tu verras",
            "Il était une fois" to "J’ai encore rêvé d’elle",
            "Eddy Mitchell" to "Couleur menthe à l’eau",
            "Maurane" to "Sur un prélude de Bach",
            "Pauline Julien" to "L’âme à la tendresse",
            "Mireille Mathieu" to "Mille colombes",
            "Johnny Hallyday" to "Quelque chose de Tennessee",
            "Serge Reggiani" to "Il suffirait de presque rien",
            "Jacques Brel" to "La quête",
            "Léo Ferré" to "Vingt ans",
            "Laurent Voulzy" to "Le coeur grenadine",
            "Félix Leclerc" to "Le tour de l’île",
            "Georges D’or" to "La Manic",
            "Charles Aznavour" to "La Mamma",
            "Charles Trenet" to "Y a d’la joie",
            "Pierre Perret" to "Lily",
            "Dick Annegarn" to "Bruxelles",
            "France Gall" to "Si maman si",
            "Charles Trenet" to "Douce France",
            "Jacques Brel" to "Amsterdam"
        )

        addSongArtistToPlaylist(
            basicalBlindTest,
            "Rasputin" to "Boney M.",
            "September" to "Earth, Wind & Fire",
            "Sarà perché ti amo" to "Ricchi e Poveri",
            "Halo" to "Beyoncé",
            "L\'aventurier" to "Indochine",
            "J\'Irai Ou Tu Iras" to "Céline Dion;Jean-Jacques Goldman",
            "Africa" to "Toto",
            "I\'m So Excited" to "The Pointer Sisters",
            "Besoin de rien, envie de toi" to "Peter & Sloane",
            "Cheap Thrills" to "Sia, Sean Paul",
            "Wati by Night" to "Sexion D\'Assaut",
            "Beggin\'" to "Måneskin",
            "Another Love" to "Tom Odell",
            "Pumped Up Kicks" to "Foster the People",
            "Uptown Funk" to "Mark Ronson, Bruno Mars",
            "Wake Me Up Before You Go-Go" to "Wham!",
            "Je te donne" to "Jean-Jacques Goldman, Michael Jones",
            "Sous le vent" to "Céline Dion, Garou",
            "Billie Jean" to "Michael Jackson",
            "Sweet Dreams" to "Eurythmics, Annie Lennox, Dave Stewart",
            "Highway to Hell" to "AC/DC",
            "Come and Get Your Love " to "Redbone",
            "Dernière danse" to "Kyo",
            "Là-bas" to "Jean-Jacques Goldman, Sirima",
            "Bohemian Rhapsody" to "Queen",
            "CAN\'T STOP THE FEELING!" to "Justin Timberlake",
            "Old Town Road" to "Lil Nas X, Billy Ray Cyrus",
            "Ma direction" to "Sexion D\'Assaut",
            "Footloose" to "Kenny Loggins",
            "I\'m Outta Love" to "Anastacia",
            "It\'s Raining Men" to "The Weather Girls",
            "Knockin\' On Heaven\'s Door" to "Bob Dylan",
            "Get Lucky" to "Daft Punk, Pharrell Williams, Nile Rodgers",
            "Waka Waka" to "Shakira, Freshlyground",
            "Eye of the Tiger" to "Survivor",
            "Je t\'aimais, je t\'aime, je t\'aimerai" to "Francis Cabrel",
            "Seven Nation Army" to "The White Stripes",
            "Ma Benz" to "Suprême NTM, Lord Kossity",
            "Hey Ya!" to "Outkast",
            "Cry Me A River" to "Justin Timberlake",
            "Shut Up and Let Me Go" to "The Ting Tings",
            "Hey, Soul Sister" to "Train",
            "Girls Just Want to Have Fun" to "Cyndi Lauper",
            "Fallin\'" to "Alicia Keys",
            "Thank You" to "Dido",
            "Wonderwall" to "Oasis",
            "All Of Me" to "John Legend",
            "Sexual Healing" to "Marvin Gaye",
            "Let Her Go" to "Passenger",
            "Still Loving You" to "Scorpions",
            "Should I Stay or Should I Go" to "The Clash",
            "Heavy Cross" to "Gossip",
            "Hallelujah" to "Jeff Buckley",
            "Ma philosophie" to "Amel Bent",
            "Maria Maria" to "Santana, The Product G&B",
            "Sapés comme jamais" to "Maître Gims, Niska",
            "Djadja" to "Aya Nakamura",
            "Timber" to "Pitbull, Kesha",
            "Let\'s Groove" to "Earth, Wind & Fire",
            "I Know What You Want" to "Busta Rhymes, Mariah Carey, Flipmode Squad",
            "The Final Countdown" to "Europe",
            "Come" to "Jain",
            "I Love Rock \'N Roll" to "Joan Jett and the Blackhearts",
            "Les Champs-Elysées" to "Joe Dassin",
            "Careless Whisper" to "George Michael",
            "Dreams" to "Fleetwood Mac",
            "Cheerleader" to "OMI",
            "Killing Me Softly With His Song" to "Fugees, Ms. Lauryn Hill",
            "Crazy In Love" to "Beyoncé, JAY-Z",
            "It Ain\'t Me" to "Kygo, Selena Gomez",
            "Don\'t Stop The Music" to "Rihanna",
            "Million Eyes" to "Loïc Nottet",
            "Elle a les yeux revolver" to "Marc Lavoine",
            "No Scrubs" to "TLC",
            "Last Nite" to "The Strokes",
            "The Time of My Life" to "Bill Medley, Jennifer Warnes",
            "I Wanna Dance with Somebody" to "Whitney Houston",
            "Trois nuits par semaine" to "Indochine",
            "Ain\'t No Sunshine" to "Bill Withers",
            "Sign of the Times" to "Harry Styles",
            "How To Save A Life" to "The Fray",
            "Streets of Philadelphia" to "Bruce Springsteen",
            "Thriller" to "Michael Jackson",
            "Jailhouse Rock" to "Elvis Presley",
            "Mambo No. 5" to "Lou Bega",
            "Daddy Cool" to "Boney M.",
            "Toxic" to "Britney Spears",
            "Walk Like an Egyptian" to "The Bangles",
            "La même" to "Maître Gims, Vianney",
            "Mr. Blue Sky" to "Electric Light Orchestra",
            "Walk This Way" to "Run DMC, Aerosmith",
            "Say My Name" to "Destiny\'s Child",
            "If I Ain\'t Got You" to "Alicia Keys",
            "Rockollection" to "Laurent Voulzy",
            "Total Eclipse of the Heart" to "Bonnie Tyler",
            "Ça m\\'énerve" to "Helmut Fritz",
            "Personal Jesus" to "Depeche Mode",
            "7 Seconds" to "Youssou N\'Dour, Neneh Cherry",
            "Pour Que Tu M\'Aimes Encore" to "Céline Dion",
            "Du côte de chez Swann" to "Dave",
            "...Baby One More Time" to "Britney Spears",
            "Yeah!" to "Usher Featuring Lil\' Jon & Ludacris",
            "I Gotta Feeling" to "Black Eyed Peas",
            "Human" to "Rag\'n\'Bone Man",
            "Uptown Girl" to "Billy Joel",
            "The Sound of Silence" to "Simon & Garfunkel",
            "Suzanne" to "Leonard Cohen",
            "Genie In a Bottle" to "Christina Aguilera",
            "Est-ce que tu m\'aimes ?" to "Maître Gims",
            "Killing In The NameE" to "Rage Against the Machine",
            "Enjoy the Silence" to "Depeche Mode",
            "Budapest" to "George Ezra",
            "Sweater Weather" to "The Neighbourhood",
            "Le temps est bon" to "Bon Entendeur, Isabelle Pierre",
            "Unstoppable" to "Sia",
            "Sur ma route" to "Black M",
            "Happy" to "Pharrell Williams",
            "Beautiful Girls" to "Sean Kingston",
            "SexyBack" to "Justin Timberlake, Timbaland",
            "Coco Câline" to "Julien Doré",
            "Chandelier" to "Sia",
            "Material Girl" to "Madonna",
            "Il est cinq heures, Paris s\'éveille" to "Jacques Dutronc",
            "All About That Bass" to "Meghan Trainor",
            "J\'ai demandé à la lune" to "Indochine",
            "Place des grands hommes" to "Patrick Bruel",
            "Single Ladies" to "Beyoncé",
            "You Spin Me Round" to "Dead or Alive",
            "You Really Got Me" to "The Kinks"
        )

        addSongArtistToPlaylist(
            movieThemeSongs,
            "E.T." to "Composer: John Williams",
            "Jurassic Park" to "John Williams",
            "Star Wars" to "John Williams",
            "Pinocchio" to "Ned Washington & Leigh Harline",
            "Schindler\'s List" to "John Williams",
            "The Magnificent Seven" to "Elmer Bernstein",
            "The Bridge on the River Kwai" to "Malcolm Arnold",
            "The Italian Job" to "Quincy Jones",
            "The Terminator" to "Brad Fiedel",
            "Chitty Chitty Bang Bang" to "Sherman Brothers",
            "Gone with the Wind" to "Max Steiner",
            "Viva Las Vegas" to "Elvis Presley",
            "9 to 5" to "Dolly Parton",
            "Back to the Future" to "Huey Lewis & The News",
            "The Dam Busters" to "Eric Coates",
            "The Good, the Bad and the Ugly" to "Ennio Morricone",
            "The Great Escape" to "Elmer Bernstein",
            "Mars Attacks!" to "Danny Elfman",
            "Ghostbusters" to "Ray Parker Jr",
            "Robin Hood" to "Roger Miller",
            "Don\'t Look Now" to "Pino Donaggio",
            "Cape Fear" to "Bernard Herrmann",
            "Hatari" to "Henry Mancini",
            "The Sting" to "Scott Joplin",
            "The Pink Panther" to "Henry Mancini",
            "Lawrence of Arabia" to "Maurice Jarre",
            "The Witches" to "Stanley Myers",
            "Raiders of the Lost Ark" to "John Williams",
            "James Bond : Dr. No" to "John Barry",
            "From Russia with Love" to "Matt Monro",
            "The King of Comedy" to "Van Morrison",
            "A Clockwork Orange" to "Walter Carlos",
            "Beauty and the Beast" to "Alan Menken & Howard Ashman",
            "Star Trek" to "Jerry Goldsmith",
            "Jaws" to "John Williams",
            "Taxi Driver" to "Bernard Herrmann",
            "Robin Hood: Prince of Thieves" to "Bryan Adams",
            "Thunderball" to "Tom Jones",
            "GoldenEye" to "Tina Turner",
            "Goldfinger" to "Shirley Bassey",
            "Fiddler on the Roof" to "Topol",
            "Live and Let Die" to "Paul McCartney & Wings",
            "The French Connection" to "Don Ellis",
            "Braveheart" to "James Horner",
            "Airplane!" to "Elmer Bernstein",
            "The Eagle Has Landed" to "Lalo Schifrin",
            "The Big Country" to "Jerome Moross",
            "Babe" to "Nigel Westlake",
            "Blade Runner" to "Vangelis",
            "Get Carter" to "Roy Budd",
            "The Godfather" to "Nino Rota & Ennio Morricone",
            "Saving Private Ryan" to "John Williams",
            "Calamity Jane" to "Doris Day",
            "Mary Poppins" to "Sherman Brothers",
            "Halloween" to "John Carpenter",
            "Tom and Jerry: The Movie" to "Henry Mancini",
            "The Little Shop of Horrors" to "Fred Katz & Ronald Stein",
            "Hook" to "John Williams",
            "Jailhouse Rock" to "Elvis Presley",
            "Sweet Sweetback\'s Baadass Song" to "Melvin Van Peebles",
            "A Hard Day\'s Night" to "Beatles",
            "The Jungle Book" to "George Bruns & the Sherman Brothers",
            "Saturday Night Fever" to "Bee Gees",
            "Labyrinth" to "David Bowie",
            "West Side Story" to "Saul Chaplin, Johnny Green, Sid Ramin and Irwin Kostal",
            "Close Encounters of the Third Kind" to "John Williams",
            "The Hunchback of Notre Dame" to "Paul Kandel",
            "Chicken Run" to "John Powell & Harry Gregson Williams",
            "Team America: World Police" to "Marc Shairman",
            "The Sword in the Stone" to "George Bruns & The Sherman Brothers",
            "The AristoCats" to "Maurice Chevalier",
            "Amistad" to "John Williams",
            "Ace Ventura: Pet Detective" to "Ira Newborn",
            "On Her Majesty\'s Secret Service" to "John Barry",
            "Dirty Harry" to "Lalo Schrifin",
            "The Wizard of Oz" to "Judy Garland",
            "Catch Me If You Can" to "John Williams",
            "The Man with the Golden Gun" to "Lulu",
            "Diamonds Are Forever" to "Shirley Bassey",
            "Flash Gordon" to "Queen",
            "JFK" to "John Williams",
            "The Triplets of Belleville" to "Benoit Charest & Sylvain Chomet",
            "South Park" to "Trey Parker, Matt Stone & Mary Kay Bergman",
            "Superman" to "John Williams",
            "Tokyo Drifter" to "So Kaburagi",
            "2001: A Space Odyssey" to "Alex North",
            "The Sound of Music" to "Julie Andrews",
            "The Towering Inferno" to "John Williams",
            "The Living Daylights" to "A\'ha",
            "For Your Eyes Only" to "Sheena Easton",
            "Moonraker" to "Shirley Bassey",
            "The Lord of the Rings" to "Howard Shore",
            "Sweeney Todd" to "Stephen Sondheim",
            "The Poseidon Adventure" to "John Williams",
            "Zulu" to "John Barry",
            "Alice in Wonderland" to "Oliver Wallace",
            "Sleeping Beauty" to "Tchaikovsky",
            "Harry Potter" to "",
            "La soupe aux choux" to "",
            "Interstallar" to "Hans Zimmer",
            "Pirate of the Caribbean" to "Hans Zimmer",
            "Mission Impossible" to "",
            "Indiana Jones" to "John Williams"
        )

        addSongArtistToPlaylist(
            videoGames,
            "The legend of Zelda" to "Kōji Kondō",
            "The wind waker" to "Kōji Kondō",
            "Super Mario Bros." to "Kōji Kondō",
            "Super Smash Bros : Brawl" to "",
            "Still alive" to "Mike Morasky",
            "Skyrim" to "Jeremy Soule",
            "Wii sport" to "",
            "Starfox" to "Hajime Hirasawa",
            "Doom" to "Robert Prince",
            "Quake" to "Trent Reznor",
            "Halo" to "Martin O\'Donnell and Michael Salvatori",
            "Final fantasy 7" to "Nobuo Uematsu",
            "Final fantasy" to "",
            "Oblivion" to "Jeremy Soule",
            "Silent Hill" to "Akira Yamaoka",
            "Castlevania" to "Michiru Yamane",
            "Pokemon" to "",
            "Fallout 3" to "Inon Zur",
            "Minecraft" to "C418",
            "Tetris" to "",
            "Among us" to "",
            "The last of us" to "",
            "Pokemon Red" to "",
            "Pac man" to "",
            "Sonic" to "",
            "Civilization" to "Christopher Tin",
            "Assassin\'s Creed" to "",
        )

        addSongArtistToPlaylist(
            classicalHits,
            "Eine kleine Nachtmusik" to "Wolfgang Amadeus Mozart",
            "Für Elise" to "Ludwig van Beethoven",
            "Symphony no.9" to "Ludwig van Beethoven",
            "Symphony no.5" to "Ludwig van Beethoven",
            "Dies Irae" to "Giuseppe Verdi",
            "Lacrimosa" to "Wolfgang Amadeus Mozart",
            "Also sprach Zarathustra" to "Richard Wagner",
            "Ride of the Valkyries" to "Richard Wagner",
            "The blue Danube" to "Johann Strauss II",
            "Toccata" to "Johann Sebastian Bach",
            "Spring" to "Antonio Vivaldi",
            "Summer" to "Antonio Vivaldi",
            "Winter" to "Antonio Vivaldi",
            "Nabucco" to "Giuseppe Verdi",
            "La Traviatta" to "Giuseppe Verdi",
            "O Fortuna" to "Carl Orff",
            "From the new world" to "Antonin Dvorak",
            "Hungarian Dance" to "Johannes Brahms",
            "Swan Lake" to "Pyotr Ilyich Tchaikovsky",
            "Gymnopédie" to "Erik Satie",
            "Carmen" to "Georges Bizet",
            "Moonlight sonata" to "Ludwig van Beethoven",
            "Symphony no.40" to "Wolfgang Amadeus Mozart",
            "L'arlésienne" to "Georges Bizet",
            "The magic flute" to "Wolfgang Amadeus Mozart",
            "Water music" to "George Frideric Handel",
            "West side story" to "Leonhard Bernstein",
            "Ave Maria" to "Franz Schubert",
            "Nutcracker" to "Pyotr Ilyich Tchaikovsky",
            "Bolero" to "Maurics Ravel",
            "Wedding March" to "Felix Mendelssohn",
            "Rigoletto" to "Giuseppe Verdi",
            "Le carnaval des animaux" to "Camille Saint-Saens",
            "The planets" to "Gustav Holst",
            "Romeo and Juliet" to "Sergei Prokofiev",
            "Turandot" to "Giacomo Puccini",
            "Radetsky March" to "Johann Strauss I",
            "William Tell" to "Gioachino Rossini",
            "Alleluia" to "George Frideric Handel",

        )
    }

    private fun addSongArtistToPlaylist(playlist: Playlist, vararg songs: Pair<String, String>) {
        songs.forEach {
            playlist.addSong(Song(it.first, it.second))
        }
    }
    private fun addArtistSongToPlaylist(playlist: Playlist, vararg songs: Pair<String, String>) {
        songs.forEach {
            playlist.addSong(Song(it.first, it.second))
        }
    }
}
