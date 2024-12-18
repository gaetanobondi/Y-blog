let currentPostIndex = 0;
        const posts = [];
        let additionalPostsLoaded = false; // Flag per sapere se abbiamo già caricato post2.xml

        // Carica i dati dal file XML specificato
        function loadPosts() {
            fetch(`http://localhost:8080/api/posts/random`)
                .then(response => response.text())
                .then(str => {
                    const parser = new DOMParser();
                    const xmlDoc = parser.parseFromString(str, "application/xml");
                    const postElements = xmlDoc.getElementsByTagName('post');
                    for (let i = 0; i < postElements.length; i++) {
                        const post = postElements[i];
                        const postData = {
                            author: post.getElementsByTagName('author')[0].textContent,
                            createdAt: post.getElementsByTagName('createdAt')[0].textContent,
                            message: post.getElementsByTagName('message')[0].textContent,
                            comments: []
                        };
                        const comments = post.getElementsByTagName('comment');
                        for (let j = 0; j < comments.length; j++) {
                            const comment = comments[j];
                            postData.comments.push({
                                author: comment.getElementsByTagName('author')[0].textContent,
                                createdAt: comment.getElementsByTagName('createdAt')[0].textContent,
                                text: comment.getElementsByTagName('text')[0].textContent
                            });
                        }
                        posts.push(postData);
                    }
                    displayPosts();
                });
        }

        // Visualizza i post
        function displayPosts() {
            const postList = document.getElementById('postList');
            for (let i = currentPostIndex; i < currentPostIndex + 2 && i < posts.length; i++) {
                const post = posts[i];
                const postElement = document.createElement('div');
                postElement.className = 'post';
                postElement.innerHTML = `
                    <div class="user">${post.author}</div>
                    <p>${post.message}</p>
                    <a href="javascript:void(0)" onclick="openModal(${i})">Visualizza completo</a>
                `;
                postList.appendChild(postElement);
            }
            currentPostIndex += 2;
        }

        // Apre la finestra modale
        function openModal(postIndex) {
            const modal = document.getElementById("myModal");
            const modalContent = document.getElementById("modalContent");
            const post = posts[postIndex];
            
            modalContent.innerHTML = `
                <section class="post-details">
                    <div class="post">
                        <div class="user">${post.author}</div>
                        <p>${post.text}</p>
                        <div class="post-info">
                            <span class="date">${post.createdAt} ${post.time}</span>
                            <span class="likes">❤️ 10</span> 
                            <span class="retweets">🔁 5</span> 
                        </div>
                    </div>

                    <div class="comments-section">
                        <div class="add-comment">
                            <textarea id="comment-input" placeholder="Aggiungi un commento..." maxlength="280"></textarea>
                            <button id="submit-comment">Commenta</button>
                        </div>
                        <hr>
                        <h3>Commenti</h3>
                        <div id="comments-container">
                            ${post.comments.map(comment => `
                                <div class="comment">
                                    <div class="comment-user">${comment.author}</div>
                                    <p>${comment.text}</p>
                                </div>
                            `).join('')}
                        </div>
                    </div>
                </section>
            `;
            
            modal.style.display = "block";
        }


        // Chiude la finestra modale
        function closeModal() {
            const modal = document.getElementById("myModal");
            modal.style.display = "none";
        }

        // Carica altri post da post2.xml
        function loadMorePosts() {
            if (!additionalPostsLoaded) {
                loadPosts('post2.xml'); // Carica i post da post2.xml solo una volta
                additionalPostsLoaded = true;
            } else {
                displayPosts(); // Se post2.xml è già stato caricato, visualizza solo i successivi post
            }

            // Nascondi il pulsante se tutti i post sono stati caricati
            if (currentPostIndex >= posts.length) {
                const loadMoreButton = document.getElementById('loadMore');
                if (loadMoreButton) {
                    loadMoreButton.style.display = 'none'; // Nasconde il pulsante
                }
            }
        }

        async function newPost() {
            try {
                let author = 'Gaetano';
                let message = document.getElementById('new-post');
                const response = await fetch('http://localhost:8080/api/posts', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Access-Control-Allow-Origin': '*'
                    },
                    body: JSON.stringify({
                        author: author,
                        message: message
                    })
                });
                const posts = await response.json();
                console.log(posts);
            } catch (error) {
                console.error('Errore:', error);
            }
        }


        // Inizializza la pagina caricando i post da post1.xml
        loadPosts();