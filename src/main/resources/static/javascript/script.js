let currentPostIndex = 0;
let posts = [];
let page = 2;
let stop = false;

// Carica i dati dal file XML specificato
function loadPosts() {
    if (stop) return; // Evita di fare richieste se abbiamo raggiunto la fine dei post

    fetch(`http://localhost:8080/api/posts/random?page=${page}`)
        .then(response => response.text())
        .then(str => {
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(str, "application/xml");
            const postElements = xmlDoc.getElementsByTagName('post');
            const loadMoreButton = document.getElementById('loadMore');

            if(postElements.length > 0) {
                page++; // Aumenta la pagina per il prossimo caricamento
                for (let i = 0; i < postElements.length; i++) {
                    const post = postElements[i];
                    const postData = {
                        id: post.getElementsByTagName('id')[0].textContent,
                        author: post.getElementsByTagName('author')[0].textContent,
                        createdAt: post.getElementsByTagName('createdAt')[0].textContent,
                        message: post.getElementsByTagName('message')[0].textContent,
                        comments: []
                    };

                    const comments = post.getElementsByTagName('comment');
                    if(comments.length > 0) {
                        for (let j = 0; j < comments.length; j++) {
                            const comment = comments[j];
                            postData.comments.push({
                                id: comment.getElementsByTagName('id')[0].textContent,
                                author: comment.getElementsByTagName('author')[0].textContent,
                                createdAt: comment.getElementsByTagName('createdAt')[0].textContent,
                                text: comment.getElementsByTagName('text')[0].textContent
                            });
                        }
                    }

                    posts.push(postData); // Aggiungi i nuovi post alla lista
                }
                displayPosts();
                loadMoreButton.style.display = 'inline'; // Mostra il pulsante "Carica altri"
            } else {
                if(page === 1) {
                    let postList = document.getElementById('postList');
                    postList.innerHTML = '<p>Ancora nessun post.</p>';
                }
                stop = true; // Ferma il caricamento dei post se non ce ne sono più
                loadMoreButton.style.display = 'none'; // Nascondi il pulsante
            }
        })
        .catch(error => {
            console.error('Errore nel caricamento dei post:', error);
            stop = true;
        });
}

// Visualizza i post
function displayPosts() {
    const postList = document.getElementById('postList');
    for (let i = currentPostIndex; i < currentPostIndex + 5 && i < posts.length; i++) {
        const post = posts[i];
        const postElement = document.createElement('div');
        postElement.className = 'post';
        postElement.setAttribute("data-postid", post.id);
        postElement.innerHTML = `
            <div class="user" id="author">${post.author}</div>
            <p id="message">${post.message}</p>
            <div class="post-info">
                <span class="date" id="createdAt">${post.createdAt}</span>
            </div>
            <a href="javascript:void(0)" onclick="openModal(this)">Visualizza completo</a>
        `;
        postList.appendChild(postElement);
    }
    currentPostIndex += 5; // Incrementa l'indice per visualizzare i prossimi post
}

document.addEventListener('DOMContentLoaded', () => {
    const modalLinks = document.querySelectorAll('.viewModal');
    modalLinks.forEach(link => {
        link.addEventListener('click', (event) => {
            openModal(event.target);
        });
    });

    document.getElementById("submitNewPost").addEventListener("click", async () => {
        try {
            let author = 'Gaetano';
            let message = document.getElementById('new-post');
            const response = await fetch('http://localhost:8080/api/post', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Access-Control-Allow-Origin': '*'
                },
                body: JSON.stringify({
                    author: author,
                    messageText: message.value
                })
            });

            // Reset variabili
            page = 1;
            stop = false;
            currentPostIndex = 0;
            posts = []; // Pulisci i post precedenti
            const postList = document.getElementById('postList');
            postList.innerHTML = ''; // Pulisci la lista dei post
            message.value = '';
            loadPosts(); // Ricarica i post
        } catch (error) {
            console.error('Errore:', error);
        }
    });
});

// Apre la finestra modale
function openModal(event) {
    const parent = event.closest('.post'); // Supponendo che .post sia il contenitore
    const id = parent.getAttribute('data-postid');
    const author = parent.querySelector('#author');
    const createdAt = parent.querySelector('#createdAt');
    const message = parent.querySelector('#message');

    let post = {
        id: id,
        author: author?.textContent,
        createdAt: createdAt?.textContent,
        message: message?.textContent,
    };
    const modal = document.getElementById("myModal");
    const modalContent = document.getElementById("modalContent");
    const formattedDate = new Date(post.createdAt).toLocaleDateString('it-IT', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    modalContent.innerHTML = `
        <section class="post-details">
            <div class="post">
                <div class="user">${post.author}</div>
                <p>${post.message}</p>
                <div class="post-info">
                    <span class="date">${formattedDate}</span>
                </div>
            </div>

            <div class="comments-section">
                <div class="add-comment">
                    <textarea id="comment-input" placeholder="Aggiungi un commento..." maxlength="280"></textarea>
                    <button id="submit-comment" onclick="newComment(${post.id})">Commenta</button>
                </div>
                <hr>
                <h3>Commenti</h3>
                <div id="comments-container"></div>
            </div>
        </section>
    `;

    getComments(post.id);
    modal.style.display = "block";
}

function closeModal() {
    const modal = document.getElementById("myModal");
    modal.style.display = "none";
}

// Funzioni per commenti (restano invariati)


async function newComment(postId) {
            try {
                let author = 'Gaetano';
                let message = document.getElementById('comment-input');
                let response = await fetch(`http://localhost:8080/api/post/${postId}/comment`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Access-Control-Allow-Origin': '*'
                    },
                    body: JSON.stringify({
                        author: author,
                        commentText: message.value
                    })
                })
                console.log('Commento postato.', response);
                message.value = '';
                await getComments(postId);
            } catch(err) {
                console.log('Si è verificato un errore: ', err);
            }
        }

        async function getComments(postId) {
            try {
                let response = await fetch(`http://localhost:8080/api/post/${postId}/comments`);
                let text = await response.text();
                const parser = new DOMParser();
                const xmlDoc = parser.parseFromString(text, "application/xml");
                const comments = xmlDoc.getElementsByTagName('comment');
                let commentsData = {
                    comments: []
                };
                if(comments.length > 0) {
                    for (let j = 0; j < comments.length; j++) {
                        const comment = comments[j];
                        commentsData.comments.push({
                            id: comment.getElementsByTagName('id')[0].textContent,
                            author: comment.getElementsByTagName('author')[0].textContent,
                            createdAt: comment.getElementsByTagName('createdAt')[0].textContent,
                            text: comment.getElementsByTagName('text')[0].textContent
                        });
                    }
                }

                // mostro i commenti
                let commentsContainer = document.getElementById('comments-container');

                if (commentsData.comments.length === 0) {
                    // Nessun commento presente
                    commentsContainer.innerHTML = `<p>Nessun commento presente.</p>`;
                } else {
                    // Mostra i commenti
                    commentsContainer.innerHTML = `
                        ${commentsData.comments.map(comment => `
                            <div class="comment">
                                <div class="comment-user">${comment.author}</div>
                                <p>${comment.text}</p>
                            </div>
                        `).join('')}
                    `;
                }
            } catch(err) {
                console.log('Si è verificato un errore: ', err);
            }
        }