# **Maze Hide and Seek** - Plugin para Minecraft 1.8.9  

Um plugin de teste de **Hide and Seek em labirinto** para 1.8.9, onde **Hiders** tentam escapar enquanto **Seekers** os perseguem!  

## **📌 Recursos**  
✅ **Sistema de Times**:  
   - **Hiders** (escondedores) devem fugir do labirinto.  
   - **Seekers** (perseguidores) têm habilidades especiais para caçá-los.  

✅ **Skins customizadas**:  
   - Cada time tem uma skin diferente para fácil identificação.  

✅ **Sistema de cooldown**:  
   - Seekers têm um tempo de espera antes de começar a perseguir.  

✅ **Efeitos sonoros e visuais**:  
   - Sons e partículas para melhor imersão.  

✅ **Scoreboard dinâmico**:  
   - Mostra tempo restante, jogadores vivos e outras informações.  

✅ **Webhook para Discord**:  
   - Registra início/fim de partidas em um canal do Discord.  

✅ **Configuração flexível**:  
   - Tempo de jogo, spawns, saídas e muito mais configuráveis.  

---

## **⚙️ Instalação**  
1. Baixe o `.jar` na seção [Releases](https://github.com/whoiserick/maze-hide-and-seek/releases).  
2. Coloque na pasta `plugins` do seu servidor.  
3. Reinicie o servidor.  
4. Configure o arquivo `config.yml` gerado.  

---

## **📋 Comandos**  
| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/join <hider\|seeker>` | Entra no jogo como Hider ou Seeker | `maze.join` |
| `/forcestart` | Inicia o jogo manualmente | `maze.admin` |
| `/stopgame` | Cancela a partida atual | `maze.admin` |

---

## **🛠️ Configuração (config.yml)**  
```yaml
# Configurações do Maze Hide and Seek

# Configurações de tempo (em segundos)
game:
  duration: 300       # Duração total da partida
  countdown: 30       # Tempo de contagem regressiva inicial
  seeker-delay: 10    # Atraso para seekers começarem
  mid-game-join: false # Permitir entrar no meio do jogo
  needed-votes: 3     # Votos necessários para cancelar o jogo

# Configurações de webhook
webhook:
  url: ""            # URL do webhook Discord
  enabled: false     # Habilitar webhook

# Skins dos times (URLs de texturas)
skins:
  hider: "http://textures.minecraft.net/texture/1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a1a"
  seeker: "http://textures.minecraft.net/texture/2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b2b"

# Configurações do mapa
map:
  name: "Labirinto 1"
  world: "world"

  # Spawn dos times
  spawn:
    hiders:
      x: 100.5
      y: 70.0
      z: 100.5
      yaw: 0.0
      pitch: 0.0
    seekers:
      x: 150.5
      y: 70.0
      z: 150.5
      yaw: 0.0
      pitch: 0.0

  # Saídas do labirinto
  exits:
    - x: 200.5
      y: 65.0
      z: 200.5
      radius: 3.0
    - x: 50.5
      y: 65.0
      z: 50.5
      radius: 3.0

# Configurações de itens
items:
  spawn-interval: 30  # Intervalo entre spawns (segundos)
  spawn-points:
    - x: 100.5
      y: 70.0
      z: 100.5
    - x: 120.5
      y: 70.0
      z: 90.5

# Efeitos especiais
effects:
  cooldown-attack: 2  # Cooldown entre ataques (segundos)
  seeker-speed-duration: 30  # Duração do efeito de velocidade
  seeker-speed-level: 1  # Nível do efeito (0 = I, 1 = II)
  golden-apple-invisibility: 15  # Duração da invisibilidade
```

---

## **📌 Requisitos**  
- **Minecraft**: 1.8.9  
- **Spigot/Paper**: Recomendado para melhor desempenho  
- **Java**: 8+  

---

## **📜 Licença**  
MIT License - Livre para uso e modificação.  

---

## **📥 Download**  
🔗 [Releases](https://github.com/whoiserick/maze-hide-and-seek/releases)  

---
