import pyrebase
import base64
import tensorflow as tf
from tensorflow.contrib.keras.python.keras.applications.inception_v3 import *
from tensorflow.contrib.keras.python.keras.applications import InceptionV3
from tensorflow.contrib.keras.python.keras.preprocessing import image
import numpy as np
config ={"apiKey": "Your API Key", "authDomain": "clicktoknow2.firebaseapp.com", "databaseURL": "https://clicktoknow2.firebaseio.com", "storageBucket": "clicktoknow2.appspot.com"}
firebase = pyrebase.initialize_app(config)
db = firebase.database()
import h5py
from IPython.display import display
from PIL import Image
model=InceptionV3(weights='imagenet')
for i in range(0,5000):
	for j in range(0,5000):
		print("Here-1")
		all_users = db.child("User").get()
		#print("Here-2")
		#user = db.child("User").get()
		try:
            #print(user.val())
			for user in all_users.each():
				key = user.key()
				img_from_firebase = str(user.val())
				imgByte = img_from_firebase.encode()
				with open('D:\\abcd.png','wb') as fh:
					fh.write(base64.decodebytes(imgByte))
				img=image.load_img('D:\\abcd.png',target_size=(299,299))
				print("Here7")
				x=image.img_to_array(img)
				x=np.expand_dims(x,axis=0)
				x=preprocess_input(x)
				preds=model.predict(x)
				
				#print('predicted: ',decode_predictions(preds,top=5)[0])
				resList = decode_predictions(preds,top=5)[0]
				
				resNewList = []

				for tmpTup in resList:
				    tmpTup = tmpTup[1]
				    resNewList.append((tmpTup))
				db.child("Result").child(key).set(str(resNewList))
				print("set")
				db.child("User").child(key).remove()
				print("delete")
		except:
			print("except")
			continue
