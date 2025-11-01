// Create book function
function createBook(formData) {
    fetch('/api/books', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)}).then(response => response.json())
        .then(data => {
            alert('Book created successfully!');
            location.reload();
        }).catch(error => {
            console.error('Error:', error);
            alert('Failed to create book');
        });
}

// Update book function
function updateBook(bookId, formData) {
    fetch(`/api/books/${bookId}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json',},
        body: JSON.stringify(formData)
    }).then(response => response.json()).then(data => {
            alert('Book updated successfully!');
            window.location.href = '/admin';
        }).catch(error => {
            console.error('Error:', error);
            alert('Failed to update book');
        });
}

// Delete book function
function deleteBook(bookId) {
    if (!confirm('Are you sure you want to delete this book?')) {
        return;
    }

    fetch(`/api/books/${bookId}`, {
        method: 'DELETE'
    })
        .then(() => {
            alert('Book deleted successfully!');
            location.reload();
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to delete book');
        });
}

// Create book form
const createBookForm = document.getElementById('createBookForm');
if (createBookForm) {
    createBookForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = {
            isbn: document.getElementById('isbn').value,
            title: document.getElementById('title').value,
            description: document.getElementById('description').value,
            author: document.getElementById('author').value,
            publisher: document.getElementById('publisher').value,
            pictureUrl: document.getElementById('pictureUrl').value,
            price: parseFloat(document.getElementById('price').value),
            inventory: parseInt(document.getElementById('inventory').value),
            genre: document.getElementById('genre').value,
            publicationYear: parseInt(document.getElementById('publicationYear').value)
        };
        createBook(formData);
    });
}

// Edit book form
const editBookForm = document.getElementById('editBookForm');
if (editBookForm) {
    editBookForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const bookId = this.dataset.bookId;
        const formData = {
            isbn: document.getElementById('isbn').value,
            title: document.getElementById('title').value,
            description: document.getElementById('description').value,
            author: document.getElementById('author').value,
            publisher: document.getElementById('publisher').value,
            pictureUrl: document.getElementById('pictureUrl').value,
            price: parseFloat(document.getElementById('price').value),
            inventory: parseInt(document.getElementById('inventory').value),
            genre: document.getElementById('genre').value,
            publicationYear: parseInt(document.getElementById('publicationYear').value)
        };
        updateBook(bookId, formData);
    });
}

